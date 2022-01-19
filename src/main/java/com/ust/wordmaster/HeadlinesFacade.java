package com.ust.wordmaster;

import com.ust.wordmaster.controller.PostProcessor;
import com.ust.wordmaster.controller.RangedHeadlineDTO;
import com.ust.wordmaster.controller.RangedTextDTO;
import com.ust.wordmaster.service.analysing.RangeAnalyser;
import com.ust.wordmaster.service.analysing.RangedText;
import com.ust.wordmaster.service.fetching.HttpClient;
import com.ust.wordmaster.service.parsing.HTMLParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HeadlinesFacade {

    public static final String BBC_URL = "https://www.bbc.com/";
    public static final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";
    public static final String CNN_URL = "https://edition.cnn.com/";
    public static final String CNN_HEADLINES_ATTRIBUTE = null;

    private String websiteURL;

    private final HttpClient httpClient; // implementation injected by Spring
    private final HTMLParser htmlParser;
    private final RangeAnalyser rangeAnalyser;
    private final PostProcessor postProcessor;

    public HeadlinesFacade( HttpClient httpClient, HTMLParser htmlParser,
                           RangeAnalyser rangeAnalyser, PostProcessor postProcessor) {

        this.httpClient = httpClient;
        this.htmlParser = htmlParser;
        this.rangeAnalyser = rangeAnalyser;
        this.postProcessor = postProcessor;
    }

    public RangedHeadlineDTO processHeadlines(final String website, int rangeStart, int rangeEnd) {

        String headlinesAttribute;
        switch (website.toLowerCase()) {
            case "bbc":
            default:
                this.websiteURL = BBC_URL;
                headlinesAttribute = BBC_HEADLINES_ATTRIBUTE;
                break;
            case "cnn":
                this.websiteURL = CNN_URL;
                headlinesAttribute = CNN_HEADLINES_ATTRIBUTE;
                break;
        }

        log.info("-------- Fetching html from " + this.websiteURL);

        String websiteHTML = null;
        try {
            websiteHTML = this.httpClient.fetchHtml(new URL(websiteURL).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        log.info("-------- Parsing HTML page into a list of headlines with headlines attr " + headlinesAttribute);
        List<String> headlineStrings = this.htmlParser.parseHTML(websiteHTML, headlinesAttribute);

        log.info("-------- Analysing headlines against range " + rangeStart + ", " + rangeEnd);
        List<RangedText> rangedTextList = this.rangeAnalyser.findOutOfRangeWords(headlineStrings, rangeStart, rangeEnd);

        log.info("-------- Postprocessing and converting to DTO --------------");
        postProcessor.postprocess(rangedTextList, website);

        return buildRangedHeadlineDTO(rangeStart, rangeEnd, rangedTextList);

    }


    private RangedHeadlineDTO buildRangedHeadlineDTO(int rangeStart, int rangeEnd, List<RangedText> rangedTextList) {

        RangedHeadlineDTO responseDTO = new RangedHeadlineDTO();
        responseDTO.setSource(websiteURL);
        responseDTO.setRangeStart(rangeStart);
        responseDTO.setRangeEnd(rangeEnd);
        responseDTO.setDescription("Headlines processed against 5000 dictionary to show words out of the requested range");
        responseDTO.setVersion("1.0");

        List<RangedTextDTO> rangedTextDTOList = rangedTextList.stream()
                .map(text -> new RangedTextDTO(text.getText(), text.getOutOfRangeWords()))
                .collect(Collectors.toList());

        responseDTO.setRangedTextList(rangedTextDTOList);

        return responseDTO;
    }


}
