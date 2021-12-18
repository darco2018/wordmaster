package com.ust.wordmaster.newdictionary;

import lombok.Getter;

import java.util.*;

@Getter
public class CorpusDictionary5000 implements CorpusDictionary {

    private final String name;
    private final Map<String, List<DictionaryEntry>> dictionary;
    private int noOfEntries;

    public CorpusDictionary5000(String name, List<DictionaryEntry> entries) {
        Objects.requireNonNull(name);
        this.name = name;

        Objects.requireNonNull(entries);
        dictionary = new HashMap<>();
        entries.forEach(this::addEntry);
    }

    @Override
    public void addEntry(DictionaryEntry entry) {
        String headword = entry.getHeadword();
        List<DictionaryEntry> listOfEntries = this.dictionary.get(headword);
        if (listOfEntries == null) {
            listOfEntries = new ArrayList<>();
        }

        listOfEntries.add(entry);
        this.dictionary.put(headword, listOfEntries);
        noOfEntries++;
    }

    @Override
    public Map<String, List<DictionaryEntry>> getDictionaryAsMap() {
        return this.dictionary;
    }

   /* @Override
    public Map<String, List<DictionaryEntry>> getDictionarySubsetAsMap(int rangeStart, int rangeEnd) {

        return null;
    }*/

    @Override
    public boolean containsHeadword(String headword) {
        return this.dictionary.get(headword) != null;
    }

    @Override
    public List<DictionaryEntry> getEntriesByHeadword(String headword) {
        List<DictionaryEntry> entries = this.dictionary.get(headword);
        return entries == null ? new ArrayList<>() : entries;
    }

    @Override
    public boolean containsEntry(String headword, String partOfSpeech) {
        return getEntry(headword, partOfSpeech) != null;
    }

    @Override
    public DictionaryEntry getEntry(String headword, String partOfSpeech) {
        List<DictionaryEntry> entries = this.dictionary.get(headword);
        if (entries == null || entries.size() == 0)
            return null;

        for (DictionaryEntry e : entries) {
            String entryHeadword = e.getWordData().getWord();
            String entryPartOfSpeech = e.getWordData().getPartOfSpeech();
            if (entryHeadword.equalsIgnoreCase(headword) && entryPartOfSpeech.equalsIgnoreCase(partOfSpeech))
                return e;
        }

        return null;
    }

    /**
     * There can be a few entries for a given headword. The method will return
     * true if at least one of them has its rank in the given range.
     */
    boolean isHeadwordInRankRange(String headword, int rangeStart, int rangeEnd) {

        List<DictionaryEntry> entries = this.getEntriesByHeadword(headword);
        if (entries == null || entries.size() == 0)
            return false;

        for (DictionaryEntry e : entries) {
            int rank = ((WordData5000) e.getWordData()).getRank();
            if (rank >= rangeStart && rank <= rangeEnd)
                return true;
        }

        return false;
    }
}
