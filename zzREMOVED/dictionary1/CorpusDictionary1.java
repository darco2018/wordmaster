package com.ust.wordmaster.dictionary1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Immutable dictionary implemented as a set of dictionary entries
 * If a word exists as a verb and a noun, eg 'dance', it will be inserted as distinct entries: 'dance'(n) and 'dance'(v)
 */
@Slf4j
@AllArgsConstructor
public class CorpusDictionary1 {

    @Getter
    private String dictionaryName;

    @Getter  //  The map is sorted according to the natural ordering of its keys,
    private TreeSet<DictionaryEntry1> dictionary;

    public CorpusDictionary1(String name, Collection<DictionaryEntry1> entries) {
        Objects.requireNonNull(name, "Dictionary name cannot be null");
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Dictionary name cannot be empty or blank");
        }

        Objects.requireNonNull(entries, "Dictionary cannot be null");

        this.dictionaryName = name;

        this.dictionary = new TreeSet<>();
        for(DictionaryEntry1 e : entries){
            this.dictionary.add(
                    new DictionaryEntry1(new WordRoot1(e.getWordRoot().getWord()),
                            new WordData1(e.getWordData().getRank(),
                                    e.getWordData().getPartOfSpeech(),
                                    e.getWordData().getFrequency(),
                                    e.getWordData().getDispersion()))
            );
        }
    }

    // we lose TreeSet methods
    public SortedSet<DictionaryEntry1> getDictionaryAsc() {
        return Collections.unmodifiableSortedSet(dictionary);
    }
}