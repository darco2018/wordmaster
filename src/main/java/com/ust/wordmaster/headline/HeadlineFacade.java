package com.ust.wordmaster.headline;

import com.ust.wordmaster.service.fetch.HttpClient;
import com.ust.wordmaster.service.parse.HTMLParser;
import com.ust.wordmaster.service.range.PostProcessor;
import com.ust.wordmaster.service.range.RangeAnalyser;
import com.ust.wordmaster.service.range.RangedText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
public class HeadlineFacade {

    private final HttpClient httpClient;
    private final HTMLParser htmlParser;
    private final RangeAnalyser rangeAnalyser;
    private final PostProcessor postProcessor;
    private String websiteURL;
    private String headlinesAttribute;

    public HeadlineFacade(HttpClient httpClient, HTMLParser htmlParser,
                          RangeAnalyser rangeAnalyser, PostProcessor postProcessor) {

        this.httpClient = httpClient;
        this.htmlParser = htmlParser;
        this.rangeAnalyser = rangeAnalyser;
        this.postProcessor = postProcessor;
    }

    public HeadlineResponseDTO processHeadlinesFromHtmlFile(final String websiteName, int rangeStart, int rangeEnd, final Path htmlFile) {

        List<RangedText> rangedTexts = getRangedTexts(websiteName, rangeStart, rangeEnd, htmlFile);
        return buildRangedHeadlineDTO(rangeStart, rangeEnd, rangedTexts);
    }

    public HeadlineResponseDTO processHeadlinesFromServer(final String websiteName, int rangeStart, int rangeEnd) {

        List<RangedText> rangedTexts = getRangedTexts(websiteName, rangeStart, rangeEnd, null);
        return buildRangedHeadlineDTO(rangeStart, rangeEnd, rangedTexts);
    }

    private List<RangedText> getRangedTexts(String websiteName, int rangeStart, int rangeEnd, Path htmlFile) {

      setWebsiteSpecificData(websiteName);

        String html = "";
        if (htmlFile != null) {
            log.info("-------- Fetching html from file " + htmlFile);
            try {
                html = Files.readString(htmlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("-------- Fetching html from server " + this.websiteURL);
            html = fetchHTMLFromWebsite(this.websiteURL);
        }

        log.info("-------- Parsing HTML page into a list of headlines with headlines attr " + this.headlinesAttribute);
        List<String> headlines = this.htmlParser.parseHTML(html, this.headlinesAttribute);

        log.info("-------- Processing headlines for words out of range(" + rangeStart + ", " + rangeEnd +")");
        List<RangedText> rangedTexts = this.rangeAnalyser.findOutOfRangeWords(headlines, rangeStart, rangeEnd);

        log.info("-------- Postprocessing and converting to DTO --------------");
        return postProcessor.postProcess(rangedTexts, websiteName);
    }

    private void setWebsiteSpecificData(String websiteName) {

        final String BBC_URL = "https://www.bbc.com/";
        final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";
        final String CNN_URL = "https://edition.cnn.com/";
        final String CNN_HEADLINES_ATTRIBUTE = null;

        switch (websiteName.toLowerCase()) {
            case "bbc":
            default:
                this.websiteURL = BBC_URL;
                this.headlinesAttribute = BBC_HEADLINES_ATTRIBUTE;
                break;
            case "cnn":
                this.websiteURL = CNN_URL;
                this.headlinesAttribute = CNN_HEADLINES_ATTRIBUTE;
                break;
        }
    }

    private String fetchHTMLFromWebsite(String url) {

        try {
            return this.httpClient.fetchHtml(new URL(url).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
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
