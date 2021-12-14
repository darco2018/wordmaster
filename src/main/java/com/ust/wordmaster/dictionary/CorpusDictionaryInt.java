package com.ust.wordmaster.dictionary;

import java.util.NavigableSet;
import java.util.TreeSet;

public interface CorpusDictionaryInt {

    TreeSet<DictionaryEntry> getDictionary();

    boolean containsWord(String word, NavigableSet<DictionaryEntry> entries);

    NavigableSet<DictionaryEntry> getDictionarySubset(int rangeStart, int rangeEnd);

    NavigableSet<DictionaryEntry> getDictionaryByRank(boolean reversed);

    NavigableSet<DictionaryEntry> getDictionaryByWord(boolean reversed);


}
