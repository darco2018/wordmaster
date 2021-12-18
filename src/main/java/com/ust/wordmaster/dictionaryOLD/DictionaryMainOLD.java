package com.ust.wordmaster.dictionaryOLD;

import java.util.*;

public class DictionaryMainOLD {

    public static void main(String[] args) {

        DictionaryEntryOLD the = new DictionaryEntryOLD("the", 1, "a", 22038615, 0.98);
        DictionaryEntryOLD danceNoun = new DictionaryEntryOLD("dance", 1682, "n", 21799, 0.95);
        DictionaryEntryOLD danceVerb = new DictionaryEntryOLD("dance", 1978, "v", 18263, 0.94);
        DictionaryEntryOLD standVerb = new DictionaryEntryOLD("stand", 282, "v", 140937, 0.92);
        DictionaryEntryOLD till = new DictionaryEntryOLD("till", 5000, "i", 5079, 0.92);
        DictionaryEntryOLD till_copy = new DictionaryEntryOLD("till", 5000, "i", 5079, 0.92);

    /*    List<DictionaryEntry2> entries = new ArrayList<>(Arrays.asList(till_copy, the, danceNoun, danceVerb, standVerb, till));

        CorpusDictionary2 testDictionary = new CorpusDictionary2("Frequency dictionary 5000", entries);
        SortedSet<DictionaryEntry2> orderedDictionary = testDictionary.getDictionaryByRank(false);
        for (DictionaryEntry2 entry : orderedDictionary) {
            System.out.println(entry);
        }
        System.out.println(orderedDictionary.size());
*/

        ////////////////------TESTING LOADING DATA FROM FILE-------////////////////////////
        List<DictionaryEntryOLD> entriesFromFile = CSVParserOLD.parse("dictionary5000.csv");
        CorpusDictionaryOLD corpusDict2FromFile = new CorpusDictionaryOLD("Dictionary from file", entriesFromFile);

        System.out.println("----------Printing dictionary from file --------------");
        //System.out.println(corpusDict2FromFile.getDictionaryAsMap().size());
  /*      NavigableSet<DictionaryEntry2> byRankAsc = corpusDict2FromFile.getDictionaryByRank(true);
        System.out.println("--------- By rank asc: ");
        byRankAsc.stream().limit(10).forEach(System.out::println);

*/
        /////// - test alphabetically
        NavigableSet<DictionaryEntryOLD> orderedByWord = corpusDict2FromFile.getDictionaryByWord(false);
        orderedByWord.stream().limit(200).forEach(System.out::println);
        //orderedByWord.



    }


}
