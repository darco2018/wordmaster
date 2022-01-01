package com.ust.wordmaster;


import com.ust.wordmaster.controller.RangedTextResponseDTO;
import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.service.analysing.RangeAnalyser;
import com.ust.wordmaster.service.analysing.RangeAnalyser5000;
import com.ust.wordmaster.service.analysing.RangedText;
import com.ust.wordmaster.service.fetching.HttpClient;
import com.ust.wordmaster.service.fetching.HttpClientImpl;
import com.ust.wordmaster.service.parsing.HTMLParser;
import com.ust.wordmaster.service.parsing.HTMLParserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
public class BBCHeadlinesFacade {

    public static final String BBC_URL = "https://www.bbc.com/";
    public static final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";
    public static final String CNN_URL = "https://edition.cnn.com/";
    public static final String CNN_HEADLINES_ATTRIBUTE = null;
    private final CorpusDictionary corpusDictionary;

    private String websiteURL;
    private String headlinesAttribute;

    public BBCHeadlinesFacade(CorpusDictionary corpusDictionary){
        this.corpusDictionary = corpusDictionary;
    }

    public RangedTextResponseDTO fetchAndParseHeadlines(final String website, int rangeStart, int rangeEnd) {

        switch (website.toLowerCase()){
            case "bbc":
                this.websiteURL = BBC_URL;
                this.headlinesAttribute = BBC_HEADLINES_ATTRIBUTE;
            case "cnn":
                this.websiteURL = CNN_URL;
                this.headlinesAttribute = CNN_HEADLINES_ATTRIBUTE;
            default:
                this.websiteURL = BBC_URL;
                this.headlinesAttribute = BBC_HEADLINES_ATTRIBUTE;
        }

        log.info("-------- Fetching html from " +  this.websiteURL);
        HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());
        String websiteHTML = null;
        try {
            websiteHTML = fetchingService.fetchHtml(new URL(websiteURL).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        log.info("-------- Parsing HTML page into a list of headlines with headlines attr " + this.headlinesAttribute);
        HTMLParser htmlParser = new HTMLParserImpl();
        // more generic:
        // List<LineOfText> linesOfText = parser.parseTextIntoSentencesORLines(userSentText)
        List<String> headlineStrings = htmlParser.parseHTML(websiteHTML, headlinesAttribute);

        return analyseHeadlinesAgainstRange(corpusDictionary, headlineStrings, rangeStart, rangeEnd);
    }

    private RangedTextResponseDTO analyseHeadlinesAgainstRange(CorpusDictionary corpusDictionary, List<String> headlineStrings, int rangeStart, int rangeEnd) {
        log.info("-------- Analysing headlines against range ");
        RangeAnalyser rangeAnalyser = new RangeAnalyser5000(corpusDictionary);
        // get range info from User's get request
        // In case of a user's text, we don't really have to split the text into sentences(?!)
        List<RangedText> rangedTextList = rangeAnalyser.findOutOfRangeWords(headlineStrings, rangeStart, rangeEnd);
        rangedTextList.stream().limit(4).forEach(System.out::println);

        log.info("-------- Convert to DTO --------------");

        RangedTextResponseDTO responseDTO = new RangedTextResponseDTO(rangedTextList);
        return responseDTO.map();

        // HeadlinesDTO headlinesDTO = toHeadlinesDTOMapper.map(filteredWords);
        // RangedTextResponseDTO responseDTO = mapper.mapToDTO(List<RangedText> rangedTextList)
        // responseDTO is ready to be sent through controller
    }


}
