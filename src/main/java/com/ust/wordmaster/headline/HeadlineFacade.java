package com.ust.wordmaster.headline;

import com.ust.wordmaster.service.range.PostProcessor;
import com.ust.wordmaster.service.range.RangeAnalyser;
import com.ust.wordmaster.service.range.RangedText;
import com.ust.wordmaster.service.fetch.HttpClient;
import com.ust.wordmaster.service.parse.HTMLParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
public class HeadlineFacade {

    public static final String BBC_URL = "https://www.bbc.com/";
    public static final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";
    public static final String CNN_URL = "https://edition.cnn.com/";
    public static final String CNN_HEADLINES_ATTRIBUTE = null;

    private String websiteURL;

    private final HttpClient httpClient;
    private final HTMLParser htmlParser;
    private final RangeAnalyser rangeAnalyser;
    private final PostProcessor postProcessor;

    public HeadlineFacade(HttpClient httpClient, HTMLParser htmlParser,
                          RangeAnalyser rangeAnalyser, PostProcessor postProcessor) {

        this.httpClient = httpClient;
        this.htmlParser = htmlParser;
        this.rangeAnalyser = rangeAnalyser;
        this.postProcessor = postProcessor;
    }

    public HeadlineResponseDTO processHeadlines(final String website, int rangeStart, int rangeEnd) {

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
        postProcessor.postProcess(rangedTextList, website);

        return buildRangedHeadlineDTO(rangeStart, rangeEnd, rangedTextList);
    }


    private HeadlineResponseDTO buildRangedHeadlineDTO(int rangeStart, int rangeEnd, List<RangedText> rangedTextList) {

        HeadlineResponseDTO responseDTO = new HeadlineResponseDTO();
        responseDTO.setSource(websiteURL);
        responseDTO.setRangeStart(rangeStart);
        responseDTO.setRangeEnd(rangeEnd);
        responseDTO.setDescription("Headlines processed against 5000 dictionary to show words out of the requested range");
        responseDTO.setVersion("1.0");

        responseDTO.setRangedTexts(rangedTextList); // it should set it in setRangedTextList(rangedTextJSONList);

        return responseDTO;
    }


}
