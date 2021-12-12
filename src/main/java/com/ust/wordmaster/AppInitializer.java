package com.ust.wordmaster;

import com.ust.wordmaster.dict2.CSVParser2;
import com.ust.wordmaster.dict2.CorpusDictionary2;
import com.ust.wordmaster.dict2.DictionaryEntry2;
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
        List<DictionaryEntry2> entriesFromFile = CSVParser2.parse(DICTIONARY_FILE);
        CorpusDictionary2 corpusDictionary2 = new CorpusDictionary2("Corpus Dictionary from file", entriesFromFile);

        //fetch html from bbc
        log.info("-------- Loading Corpus Dictionary & fetching BBC html --------------");
        HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());
        String bbcHomepageHtml = fetchingService.fetchHtml(BBC_URL);

        log.info("-------- Parsing BBC html into a List<String> of headlines --------------");
        HTMLParser htmlParser = new HTMLParserImpl();
        List<String> headlineStrings = htmlParser.parse(bbcHomepageHtml, BBC_HEADLINES_ATTRIBUTE);
//headlineStrings = List.of("Don't lied? (kisses] copy: copies!! ((copied?! \"tries\"? tried injure injured is an artists smiles likes surprise surprised rushed"); //
    // headlineStrings = List.of("((copied", "*!copied","copied))", "copied?!", "#@copied?!", "boy's");
        //headlineStrings = List.of("taking", "crying", "lying", "sitting" );
         //headlineStrings = List.of("he's", "she'd", "boy's", "I'd"); // "I'll",


         log.info("-------- Filtering the string headlines for a range eg (1000 - 2000) --------------");
        FilteringService filteringService = new FilteringServiceImpl(corpusDictionary2);
        List<FilteredHeadline> filteredHeadlines = filteringService.filter(headlineStrings, 1, 5000);
        filteredHeadlines.stream().limit(3).forEach(System.out::println); // each FilteredHeadline has String[] words, int[] indexesOutOfRange, rangeInfo
        List<String> outOfRangeWords = ((FilteringServiceImpl)filteringService).getWordsOutOfRangeStrings();
        outOfRangeWords.stream().forEach(System.out::println);

        log.info("-------- ?!!! --------------");
        // HeadlinesDTO headlinesDTO = toHeadlinesDTOMapper.map(filteredWords);
    }
}

//"apples are red fruits",
//"Hospital hospital protein has a knack for food");
//"Jerome will develop an inkling for romance !  ");   // is, an, !
//"Starbucks be kiepski business ");