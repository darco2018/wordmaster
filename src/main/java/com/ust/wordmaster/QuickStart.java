package com.ust.wordmaster;

import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;

import java.util.List;

public class QuickStart {
    public static final String DICTIONARY_FILE = "dictionary5000.csv";

    public static void main(String[] args) {
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
       CorpusDictionary5000 corpus = new CorpusDictionary5000("Corpus Dictionary from file", entriesFromFile);
       corpus.printHeadwordsAlphabetically();
    }
}
