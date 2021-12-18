package com.ust.wordmaster.newdictionary;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class CorpusDictionary5000 implements CorpusDictionary {

    private String name;
    private Map<String, List<DictionaryEntry>> dictionary;
    private int noOfEntries;

    public CorpusDictionary5000(String name, List<DictionaryEntry> entries) {
        Objects.requireNonNull(name);
        this.name = name;

        Objects.requireNonNull(entries);
        dictionary = new HashMap<>();
        entries.stream().forEach(entry -> {

            String word = entry.getWord();
            List<DictionaryEntry> found = this.dictionary.get(word);
            if(found == null){
                found = new ArrayList<>();
                found.add(entry);
                this.dictionary.put(word,found);
            } else {
                found.add(entry);
                this.dictionary.put(word,found);
            }
            noOfEntries++;
        });
    }

    @Override
    public Map<String, List<DictionaryEntry>> getDictionary() {
        return this.dictionary;
    }

    @Override
    public Map<String, List<DictionaryEntry>> getDictionarySubset(int rangeStart, int rangeEnd) {
        return null;
    }

    @Override
    public boolean containsEntry(String word) {
        return false;
    }

    @Override
    public DictionaryEntry getDictionaryEntry(String word) {
        return null;
    }
}
