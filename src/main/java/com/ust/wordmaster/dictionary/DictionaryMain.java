package com.ust.wordmaster.dictionary;

import java.util.*;

public class DictionaryMain {

    public static void main(String[] args) {

        DictionaryEntry the = new DictionaryEntry(new WordRoot("the"), new WordData(1,
                "a",
                22038615,
                0.98));
        DictionaryEntry danceNoun = new DictionaryEntry(new WordRoot("dance"), new WordData(1682, "n", 21799, 0.95));
        DictionaryEntry danceVerb = new DictionaryEntry(new WordRoot("dance"), new WordData(1978, "v", 18263, 0.94));
        DictionaryEntry standVerb = new DictionaryEntry(new WordRoot("stand"), new WordData(282, "v", 140937, 0.92));
        DictionaryEntry till = new DictionaryEntry(new WordRoot("till"), new WordData(5000, "i", 5079, 0.92));
        DictionaryEntry till_copy = new DictionaryEntry(
                new WordRoot("till"),
                new WordData(5000, "i", 5079, 0.92));

        List<DictionaryEntry> entries = new ArrayList<>(Arrays.asList(till_copy, the, danceNoun, danceVerb, standVerb, till));

        CorpusDictionary testDictionary = new CorpusDictionary("Frequency dictionary 5000", entries);
        SortedSet<DictionaryEntry> orderedDictionary = testDictionary.getDictionaryAsc();
        for (DictionaryEntry entry : orderedDictionary) {
            System.out.println(entry);
        }

        // System.out.println(orderedDictionary);
        System.out.println(orderedDictionary.size());

        for (DictionaryEntry entry : orderedDictionary) {
            System.out.println(entry);
        }

        ////////////////------TESTING LOADING DATA FROM FILE-------////////////////////////
        List<DictionaryEntry> entriesFromFile = CSVParser.parse("dictionary5000.csv");
        CorpusDictionary corpusDictionaryFromFile = new CorpusDictionary("Dictionary from file", entriesFromFile);

        System.out.println("----------Printing dictionary from file --------------");
        System.out.println(corpusDictionaryFromFile.getDictionary().size());
        for (DictionaryEntry entry : corpusDictionaryFromFile.getDictionary()) {
            System.out.println(entry);
        }

    }


}
