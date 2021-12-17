package com.ust.wordmaster.newdictionary;

import java.util.List;
import java.util.Map;

public interface CorpusDictionary {

    Map<String, List<DictionaryEntry>> getDictionary();

    Map<String, List<DictionaryEntry>> getDictionarySubset(int rangeStart, int rangeEnd);

    boolean containsEntry(String word);

    DictionaryEntry getDictionaryEntry(String word);


}
