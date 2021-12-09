package com.ust.wordmaster.dict2;

import java.util.*;

public class DictionaryMain2 {

    public static void main(String[] args) {

        DictionaryEntry2 the = new DictionaryEntry2("the", 1, "a", 22038615, 0.98);
        DictionaryEntry2 danceNoun = new DictionaryEntry2("dance", 1682, "n", 21799, 0.95);
        DictionaryEntry2 danceVerb = new DictionaryEntry2("dance", 1978, "v", 18263, 0.94);
        DictionaryEntry2 standVerb = new DictionaryEntry2("stand", 282, "v", 140937, 0.92);
        DictionaryEntry2 till = new DictionaryEntry2("till", 5000, "i", 5079, 0.92);
        DictionaryEntry2 till_copy = new DictionaryEntry2("till", 5000, "i", 5079, 0.92);

    /*    List<DictionaryEntry2> entries = new ArrayList<>(Arrays.asList(till_copy, the, danceNoun, danceVerb, standVerb, till));

        CorpusDictionary2 testDictionary = new CorpusDictionary2("Frequency dictionary 5000", entries);
        SortedSet<DictionaryEntry2> orderedDictionary = testDictionary.getDictionaryByRank(false);
        for (DictionaryEntry2 entry : orderedDictionary) {
            System.out.println(entry);
        }
        System.out.println(orderedDictionary.size());
*/

        ////////////////------TESTING LOADING DATA FROM FILE-------////////////////////////
        List<DictionaryEntry2> entriesFromFile = CSVParser2.parse("dictionary5000.csv");
        CorpusDictionary2 corpusDict2FromFile = new CorpusDictionary2("Dictionary from file", entriesFromFile);

        System.out.println("----------Printing dictionary from file --------------");
        //System.out.println(corpusDict2FromFile.getDictionary().size());
  /*      NavigableSet<DictionaryEntry2> byRankAsc = corpusDict2FromFile.getDictionaryByRank(true);
        System.out.println("--------- By rank asc: ");
        byRankAsc.stream().limit(10).forEach(System.out::println);

*/
        /////// - test alphabetically
        NavigableSet<DictionaryEntry2> orderedByWord = corpusDict2FromFile.getDictionaryByWord(false);
        orderedByWord.stream().limit(200).forEach(System.out::println);
        //orderedByWord.



    }


}
