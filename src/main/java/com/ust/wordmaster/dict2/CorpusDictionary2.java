package com.ust.wordmaster.dict2;

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
public class CorpusDictionary2 {

    @Getter
    private String dictionaryName;

    @Getter  //  The map is sorted according to the natural ordering of its keys,
    private TreeSet<DictionaryEntry2> dictionary;

    public CorpusDictionary2(String name, Collection<DictionaryEntry2> entries) {
        Objects.requireNonNull(name, "Dictionary name cannot be null");
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Dictionary name cannot be empty or blank");
        }

        Objects.requireNonNull(entries, "Dictionary cannot be null");

        this.dictionaryName = name;

        this.dictionary = new TreeSet<>();
        for (DictionaryEntry2 e : entries) {
            this.dictionary.add(
                    new DictionaryEntry2(e.getWord(), e.getRank(), e.getPartOfSpeech(), e.getFrequency(), e.getDispersion())
            );
        }
    }

    // we lose TreeSet methods
    public NavigableSet<DictionaryEntry2> getDictionaryByRank(boolean reversed) {

        return Collections.unmodifiableNavigableSet(new TreeSet<>(reversed ? dictionary.descendingSet() : dictionary));
    }

    public NavigableSet<DictionaryEntry2> getDictionaryByWord(boolean reversed) {

        ComparatorByWord byWord = new ComparatorByWord();
        NavigableSet<DictionaryEntry2> sortedByWord = new TreeSet<>(reversed ? byWord.reversed() : byWord);
        sortedByWord.addAll(dictionary);

        return Collections.unmodifiableNavigableSet(sortedByWord);
    }

    static class ComparatorByWord implements Comparator<DictionaryEntry2> {

        @Override
        public int compare(DictionaryEntry2 e1, DictionaryEntry2 e2) {
            int compareByWord = e1.getWord().toLowerCase().compareTo(e2.getWord().toLowerCase());
            return compareByWord == 0 ?
                    e1.getPartOfSpeech().compareTo(e2.getPartOfSpeech()) :
                    compareByWord;
        }
    }


    static class ComparatorByFrequency implements Comparator<DictionaryEntry2> {

        @Override
        public int compare(DictionaryEntry2 o1, DictionaryEntry2 o2) {
            return o1.getFrequency() - o2.getFrequency();
        }
    }


}
