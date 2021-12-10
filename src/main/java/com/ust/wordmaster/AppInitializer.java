package com.ust.wordmaster;

import com.ust.wordmaster.dict2.CSVParser2;
import com.ust.wordmaster.dict2.CorpusDictionary2;
import com.ust.wordmaster.dict2.DictionaryEntry2;
import com.ust.wordmaster.service.fetching.HttpClient;
import com.ust.wordmaster.service.fetching.HttpClientImpl;
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


    //private static CorpusDictionary2 corpusDictionary2 = null;
    //private static RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    //private static HttpClient fetchingService = null;

    // using main I dont have to start the server
    public static void main(String[] args) {

        // create dictionary
        List<DictionaryEntry2> entriesFromFile = CSVParser2.parse(DICTIONARY_FILE);
        CorpusDictionary2 corpusDictionary2 = new CorpusDictionary2("Corpus Dictionary from file", entriesFromFile);

        //fetch html from bbc
        HttpClient fetchingService = new HttpClientImpl(new RestTemplateBuilder());
        String bbcHomepageHtml = fetchingService.fetchHtml(BBC_URL);
        //System.out.println(html);
        log.info("-------- Loaded Corpus Dictionary & fetched BBC html --------------");

        // parse bbc html into a List<String> of headlines
        HTMLParser htmlParser = new HTMLParserImpl();
        List<String> headlines = htmlParser.parse(bbcHomepageHtml, BBC_HEADLINES_ATTRIBUTE);
        //headlines.stream().forEach(System.out::println);


        // filter the string headlines for a range eg (1000 - 2000)
    }
}
