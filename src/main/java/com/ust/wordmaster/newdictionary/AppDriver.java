package com.ust.wordmaster.newdictionary;

import com.ust.wordmaster.service.fetching.HttpClient;
import com.ust.wordmaster.service.fetching.HttpClientImpl;
import com.ust.wordmaster.service.filteringOLD.ParsedTextUnitOLD;
import com.ust.wordmaster.service.filteringOLD.TextUnitCreator5000OLD;
import com.ust.wordmaster.service.filteringOLD.TextUnitsCreatorOLD;
import com.ust.wordmaster.service.parsing.HTMLParser;
import com.ust.wordmaster.service.parsing.HTMLParserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Slf4j
public class AppDriver {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    public static final String BBC_URL = "https://www.bbc.com/";
    public static final String BBC_HEADLINES_ATTRIBUTE = "data-bbc-title";

    public static void main(String[] args) {

        ////////////////////// create dictionary from file with corpus data //////////////////////////
        // change to NewDicitonaryEntry
        List<DictionaryEntry> entriesFromFile = CSVParser.parse(DICTIONARY_FILE);
        CorpusDictionary corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);

        /////////////// fetch html from bbc //////////////////
        log.info("-------- Loading Corpus Dictionary & fetching BBC html --------------");
        HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());
        String bbcHomepageHtml = null;
        try {
            bbcHomepageHtml = fetchingService.fetchHtml(new URL(BBC_URL).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        /////////////// parse text(html form bbc) into textunits(headlines) //////////////////

        log.info("-------- Parsing BBC html into a List<String> of headlines --------------");
        HTMLParser htmlParser = new HTMLParserImpl();
        List<String> headlineStrings = htmlParser.parseHTML(bbcHomepageHtml, BBC_HEADLINES_ATTRIBUTE);

        /////////// given a different list of texts we can parse it into other text into units(headlines, sentences, words) too /////////////
/*


        log.info("-------- Filtering the string headlines for a range eg (1000 - 2000) --------------");
        TextUnitsCreatorOLD textUnitCreator5000 = new TextUnitCreator5000OLD(corpusDictionary);

        // this is a bulk operation that will call containsWord() lots of times
        List<ParsedTextUnitOLD> filteredHeadlines = textUnitCreator5000.findOutOfRange(headlineStrings, 1, 5000);
        filteredHeadlines.stream().limit(3).forEach(System.out::println); // each FilteredHeadline has String[] words, int[] indexesOutOfRange, rangeInfo
        List<String> outOfRangeWords = ((TextUnitCreator5000OLD) textUnitCreator5000).getWordsOutOfRangeStrings();
        outOfRangeWords.forEach(System.out::println);

*/








    }
}
