package com.ust.wordmaster.newdictionary;

import lombok.extern.slf4j.Slf4j;

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
        CorpusDictionary corpusDictionary5000 = new CorpusDictionary5000("Dictionary 5000 from file", entriesFromFile);
        //CorpusDictionary corpusDictionary = new CorpusDictionary5000("Corpus Dictionary from file", entriesFromFile);


    }
}
