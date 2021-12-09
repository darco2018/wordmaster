package com.ust.wordmaster.dict2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

public class DictionaryMain2 {

    public static void main(String[] args) {

        DictionaryEntry2 the = new DictionaryEntry2("the", 1, "a", 22038615, 0.98);
        DictionaryEntry2 danceNoun = new DictionaryEntry2("dance", 1682, "n", 21799, 0.95);
        DictionaryEntry2 danceVerb = new DictionaryEntry2("dance", 1978, "v", 18263, 0.94);
        DictionaryEntry2 standVerb = new DictionaryEntry2("stand", 282, "v", 140937, 0.92);
        DictionaryEntry2 till = new DictionaryEntry2("till", 5000, "i", 5079, 0.92);
        DictionaryEntry2 till_copy = new DictionaryEntry2("till", 5000, "i", 5079, 0.92);

        List<DictionaryEntry2> entries = new ArrayList<>(Arrays.asList(till_copy, the, danceNoun, danceVerb, standVerb, till));

        CorpusDictionary2 testDictionary = new CorpusDictionary2("Frequency dictionary 5000", entries);
        SortedSet<DictionaryEntry2> orderedDictionary = testDictionary.getDictionaryAsc();
        for (DictionaryEntry2 entry : orderedDictionary) {
            System.out.println(entry);
        }

        System.out.println(orderedDictionary.size());


        ////////////////------TESTING LOADING DATA FROM FILE-------////////////////////////
        List<DictionaryEntry2> entriesFromFile = CSVParser2.parse("dictionary5000.csv");
        CorpusDictionary2 CorpusDictionary2FromFile = new CorpusDictionary2("Dictionary from file", entriesFromFile);

        System.out.println("----------Printing dictionary from file --------------");
        System.out.println(CorpusDictionary2FromFile.getDictionary().size());
        for (DictionaryEntry2 entry : CorpusDictionary2FromFile.getDictionary()) {
            System.out.println(entry);
        }

    }


}
