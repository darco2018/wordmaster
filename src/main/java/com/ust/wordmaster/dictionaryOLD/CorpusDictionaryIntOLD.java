package com.ust.wordmaster.dictionaryOLD;

import java.util.NavigableSet;
import java.util.TreeSet;

public interface CorpusDictionaryIntOLD {

    TreeSet<DictionaryEntryOLD> getDictionary();

    boolean containsWord(String word, NavigableSet<DictionaryEntryOLD> entries);

    NavigableSet<DictionaryEntryOLD> getDictionarySubset(int rangeStart, int rangeEnd);

    NavigableSet<DictionaryEntryOLD> getDictionaryByRank(boolean reversed);

    NavigableSet<DictionaryEntryOLD> getDictionaryByWord(boolean reversed);


}
