package com.ust.wordmaster;


import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import com.ust.wordmaster.service.analysing.RangeAnalyser;
import com.ust.wordmaster.service.analysing.RangeAnalyser5000;
import com.ust.wordmaster.service.analysing.RangedText;
import com.ust.wordmaster.service.fetching.HttpClient;
import com.ust.wordmaster.service.fetching.HttpClientImpl;

import com.ust.wordmaster.service.parsing.HTMLParser;
import com.ust.wordmaster.service.parsing.HTMLParserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Slf4j
public class AppInitializer {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    public static final String BBC_URL = "https://www.bbc.com/";
    public static final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";

    public static void main(String[] args) {

        // create dictionary
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        CorpusDictionary corpusDictionary = new CorpusDictionary5000("Corpus Dictionary from file", entriesFromFile);

        //fetch html from bbc
        log.info("-------- Loading Corpus Dictionary & fetching BBC html --------------");
        HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());
        String bbcHomepageHtml = null;
        try {
            bbcHomepageHtml = fetchingService.fetchHtml(new URL(BBC_URL).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        log.info("-------- Parsing BBC html into a List<String> of headlines --------------");
        HTMLParser htmlParser = new HTMLParserImpl();
        // more generic:
        // List<LineOfText> linesOfText = parser.parseTextIntoSentencesORLines(userSentText)
        List<String> headlineStrings = htmlParser.parseHTML(bbcHomepageHtml, BBC_HEADLINES_ATTRIBUTE);

        log.info("-------- Filtering the string headlines for a range eg (1000 - 2000) --------------");
        RangeAnalyser rangeAnalyser = new RangeAnalyser5000(corpusDictionary);
        // get range info from User's get request
        // In case of a user's text, we don't really have to split the text into sentences(?!)
        List<RangedText> rangedTextList = rangeAnalyser.findOutOfRangeWords(headlineStrings, 1, 5000);
        rangedTextList.stream().limit(4).forEach(System.out::println);

        log.info("-------- Convert to DTO --------------");
        // HeadlinesDTO headlinesDTO = toHeadlinesDTOMapper.map(filteredWords);
        // RangedTextResponseDTO responseDTO = mapper.mapToDTO(List<RangedText> rangedTextList)
        // responseDTO is ready to be sent through controller
    }
}
