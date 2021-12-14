package com.ust.wordmaster;

import com.ust.wordmaster.dictionary.CSVParser;
import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import com.ust.wordmaster.service.fetching.HttpClient;
import com.ust.wordmaster.service.fetching.HttpClientImpl;
import com.ust.wordmaster.service.filtering.FilteredHeadline;
import com.ust.wordmaster.service.filtering.FilteringService;
import com.ust.wordmaster.service.filtering.FilteringServiceImpl;
import com.ust.wordmaster.service.parsing.HTMLParser;
import com.ust.wordmaster.service.parsing.HTMLParserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

@Slf4j
public class AppInitializer {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    public static final String BBC_URL = "https://www.bbc.com/";
    public static final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";

    public static void main(String[] args) {

        // create dictionary
        List<DictionaryEntry> entriesFromFile = CSVParser.parse(DICTIONARY_FILE);
        CorpusDictionary corpusDictionary2 = new CorpusDictionary("Corpus Dictionary from file", entriesFromFile);

        //fetch html from bbc
        log.info("-------- Loading Corpus Dictionary & fetching BBC html --------------");
        HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());
        String bbcHomepageHtml = fetchingService.fetchHtml(BBC_URL);

        log.info("-------- Parsing BBC html into a List<String> of headlines --------------");
        HTMLParser htmlParser = new HTMLParserImpl();
        List<String> headlineStrings = htmlParser.parse(bbcHomepageHtml, BBC_HEADLINES_ATTRIBUTE);



        log.info("-------- Filtering the string headlines for a range eg (1000 - 2000) --------------");
        FilteringService filteringService = new FilteringServiceImpl(corpusDictionary2);
        List<FilteredHeadline> filteredHeadlines = filteringService.filter(headlineStrings, 1, 5000);
        filteredHeadlines.stream().limit(3).forEach(System.out::println); // each FilteredHeadline has String[] words, int[] indexesOutOfRange, rangeInfo
        List<String> outOfRangeWords = ((FilteringServiceImpl) filteringService).getWordsOutOfRangeStrings();
        outOfRangeWords.stream().forEach(System.out::println);

        log.info("-------- ?!!! --------------");
        // HeadlinesDTO headlinesDTO = toHeadlinesDTOMapper.map(filteredWords);
    }
}
