package com.ust.wordmaster.newdictionary;

import com.ust.wordmaster.service.fetching.HttpClient;
import com.ust.wordmaster.service.fetching.HttpClientImpl;
import com.ust.wordmaster.service.parsing.HTMLParser;
import com.ust.wordmaster.service.parsing.HTMLParserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

@Slf4j
public class AppDriver {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    public static final String BBC_URL = "https://www.bbc.com/";
    public static final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";

    public static void main(String[] args) {

        // create dictionary
        // change to NewDicitonaryEntry
        List<DictionaryEntry> entriesFromFile = CSVParser.parse(DICTIONARY_FILE);
        CorpusDictionary corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);

        //fetch html from bbc
        log.info("-------- Loading Corpus Dictionary & fetching BBC html --------------");
        HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());
        String bbcHomepageHtml = fetchingService.fetchHtml(BBC_URL);

        log.info("-------- Parsing BBC html into a List<String> of headlines --------------");
        HTMLParser htmlParser = new HTMLParserImpl();
        List<String> headlineStrings = htmlParser.parseHTML(bbcHomepageHtml, BBC_HEADLINES_ATTRIBUTE);


    }
}
