package com.ust.wordmaster;

import com.ust.wordmaster.dictionaryOLD.CSVParserOLD;
import com.ust.wordmaster.dictionaryOLD.CorpusDictionaryOLD;
import com.ust.wordmaster.dictionaryOLD.CorpusDictionaryIntOLD;
import com.ust.wordmaster.dictionaryOLD.DictionaryEntryOLD;
import com.ust.wordmaster.service.fetching.HttpClient;
import com.ust.wordmaster.service.fetching.HttpClientImpl;
import com.ust.wordmaster.service.filteringOLD.ParsedTextUnitOLD;
import com.ust.wordmaster.service.filteringOLD.TextUnitsCreatorOLD;
import com.ust.wordmaster.service.filteringOLD.TextUnitCreator5000OLD;
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
        List<DictionaryEntryOLD> entriesFromFile = CSVParserOLD.parse(DICTIONARY_FILE);
        CorpusDictionaryIntOLD corpusDictionary = new CorpusDictionaryOLD("Corpus Dictionary from file", entriesFromFile);

        //fetch html from bbc
        log.info("-------- Loading Corpus Dictionary & fetching BBC html --------------");
        HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());
        String bbcHomepageHtml = fetchingService.fetchHtml(BBC_URL);

        log.info("-------- Parsing BBC html into a List<String> of headlines --------------");
        HTMLParser htmlParser = new HTMLParserImpl();
        List<String> headlineStrings = htmlParser.parse(bbcHomepageHtml, BBC_HEADLINES_ATTRIBUTE);

        log.info("-------- Filtering the string headlines for a range eg (1000 - 2000) --------------");
        TextUnitsCreatorOLD textUnitCreator5000 = new TextUnitCreator5000OLD(corpusDictionary); // without the COrpusDictionary,
        // getting a subset dictionary would not be possible

        // this is a bulk operation that will call containsWord() lots of times
        List<ParsedTextUnitOLD> filteredHeadlines = textUnitCreator5000.parseIntoTextUnits(headlineStrings, 1, 5000);
        filteredHeadlines.stream().limit(3).forEach(System.out::println); // each FilteredHeadline has String[] words, int[] indexesOutOfRange, rangeInfo
        List<String> outOfRangeWords = ((TextUnitCreator5000OLD) textUnitCreator5000).getWordsOutOfRangeStrings();
        outOfRangeWords.forEach(System.out::println);

        // when new request comes, we cna still work with the same Filtering Service

        log.info("-------- ?!!! --------------");
        // HeadlinesDTO headlinesDTO = toHeadlinesDTOMapper.map(filteredWords);
    }
}
