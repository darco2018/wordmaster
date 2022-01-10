package com.ust.wordmaster.dictionary;

import java.util.List;
import java.util.Map;

public interface CorpusDictionary {

    void addEntry(DictionaryEntry entry);

    Map<String, List<DictionaryEntry>> asMap();

    boolean containsHeadword(String headword);

    boolean containsEntry(String headword, String partOfSpeach);

    List<DictionaryEntry> getEntriesByHeadword(String headword);

    DictionaryEntry getEntry(String headword, String partOfSpeech);

    int getNoOfEntries();

    boolean isHeadwordInRankRange(String headword, int rangeStart, int rangeEnd);
}
