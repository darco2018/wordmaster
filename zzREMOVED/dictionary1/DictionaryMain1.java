package com.ust.wordmaster.dictionary1;

import java.util.*;

public class DictionaryMain1 {

    public static void main(String[] args) {

        DictionaryEntry1 the = new DictionaryEntry1(new WordRoot1("the"), new WordData1(1,
                "a",
                22038615,
                0.98));
        DictionaryEntry1 danceNoun = new DictionaryEntry1(new WordRoot1("dance"), new WordData1(1682, "n", 21799, 0.95));
        DictionaryEntry1 danceVerb = new DictionaryEntry1(new WordRoot1("dance"), new WordData1(1978, "v", 18263, 0.94));
        DictionaryEntry1 standVerb = new DictionaryEntry1(new WordRoot1("stand"), new WordData1(282, "v", 140937, 0.92));
        DictionaryEntry1 till = new DictionaryEntry1(new WordRoot1("till"), new WordData1(5000, "i", 5079, 0.92));
        DictionaryEntry1 till_copy = new DictionaryEntry1(
                new WordRoot1("till"),
                new WordData1(5000, "i", 5079, 0.92));

        List<DictionaryEntry1> entries = new ArrayList<>(Arrays.asList(till_copy, the, danceNoun, danceVerb, standVerb, till));

        CorpusDictionary1 testDictionary = new CorpusDictionary1("Frequency dictionary 5000", entries);
        SortedSet<DictionaryEntry1> orderedDictionary = testDictionary.getDictionaryAsc();
        for (DictionaryEntry1 entry : orderedDictionary) {
            System.out.println(entry);
        }

        // System.out.println(orderedDictionary);
        System.out.println(orderedDictionary.size());

        for (DictionaryEntry1 entry : orderedDictionary) {
            System.out.println(entry);
        }

        ////////////////------TESTING LOADING DATA FROM FILE-------////////////////////////
        List<DictionaryEntry1> entriesFromFile = CSVParser1.parse("dictionary5000.csv");
        CorpusDictionary1 corpusDictionaryFromFile = new CorpusDictionary1("Dictionary from file", entriesFromFile);

        System.out.println("----------Printing dictionary from file --------------");
        System.out.println(corpusDictionaryFromFile.getDictionary().size());
        for (DictionaryEntry1 entry : corpusDictionaryFromFile.getDictionary()) {
            System.out.println(entry);
        }

    }


}
