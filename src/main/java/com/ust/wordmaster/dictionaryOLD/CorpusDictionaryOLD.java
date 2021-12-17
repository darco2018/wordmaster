package com.ust.wordmaster.dictionaryOLD;

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
public class CorpusDictionaryOLD implements CorpusDictionaryIntOLD {

    @Getter
    private String dictionaryName;

    @Getter
    private TreeSet<DictionaryEntryOLD> dictionary;

    public CorpusDictionaryOLD(final String name, final Collection<DictionaryEntryOLD> entries) {
        Objects.requireNonNull(name, "Dictionary name cannot be null");
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Dictionary name cannot be empty or blank");
        }

        Objects.requireNonNull(entries, "Dictionary cannot be null");

        this.dictionaryName = name;

        this.dictionary = new TreeSet<>();
        for (DictionaryEntryOLD e : entries) {
            this.dictionary.add(
                    new DictionaryEntryOLD(e.getWord(), e.getRank(), e.getPartOfSpeech(), e.getFrequency(), e.getDispersion())
            );
        }
    }

    /**
     * Searches the subset of the dictionary. The search doesn't differentiate between parts of speech,
     * so it will return the first matching word.
     * The search also ignores case.
     */


    @Override
    public boolean containsWord(final String word, NavigableSet<DictionaryEntryOLD> entries) {

        Objects.requireNonNull(word, "Word cannot be null");

       // validateRange(rangeStart, rangeEnd);

        String key = word.toLowerCase();
        // optimalisation
        //key = replace(key);

        NavigableSet<DictionaryEntryOLD> dictionarySubset = entries;
        // we don't have O(1) for word lookup. Multimap better
        for (DictionaryEntryOLD entry : dictionarySubset) {
            if (entry.getWord().equalsIgnoreCase(key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public NavigableSet<DictionaryEntryOLD> getDictionarySubset(final int rangeStart, final int rangeEnd) {

       validateRange(rangeStart, rangeEnd);

        DictionaryEntryOLD startEntry = null, endEntry = null;

        for (DictionaryEntryOLD entry : this.dictionary) {
            if (entry.getRank() == rangeStart)
                startEntry = entry;

            if (entry.getRank() == rangeEnd)
                endEntry = entry;

            if (startEntry != null && endEntry != null)
                break;
        }

        return Collections.unmodifiableNavigableSet(
                new TreeSet<>(this.dictionary.subSet(startEntry, true, endEntry, true)));
    }

    public static void validateRange(int rangeStart, int rangeEnd) {

        if (rangeStart < 1 || rangeStart > 4999) {
            throw new IllegalArgumentException("Range start must be in the range 1-4999");
        }
        if (rangeEnd < 2 || rangeEnd > 5000) {
            throw new IllegalArgumentException("Range start must be in the range 2-5000");
        }
        if (rangeStart > rangeEnd) {
            throw new IllegalArgumentException("Range start cannot be greater than rangeEnd");
        }
    }

    ///////////////////// Sorting options /////////////////////////////////

    @Override
    public NavigableSet<DictionaryEntryOLD> getDictionaryByRank(boolean reversed) {

        return Collections.unmodifiableNavigableSet(new TreeSet<>(reversed ? dictionary.descendingSet() : dictionary));
    }

    @Override
    public NavigableSet<DictionaryEntryOLD> getDictionaryByWord(boolean reversed) {

        ComparatorByWord byWord = new ComparatorByWord();
        NavigableSet<DictionaryEntryOLD> sortedByWord = new TreeSet<>(reversed ? byWord.reversed() : byWord);
        sortedByWord.addAll(dictionary);

        return Collections.unmodifiableNavigableSet(sortedByWord);
    }

    static class ComparatorByWord implements Comparator<DictionaryEntryOLD> {

        @Override
        public int compare(DictionaryEntryOLD e1, DictionaryEntryOLD e2) {
            int compareByWord = e1.getWord().toLowerCase().compareTo(e2.getWord().toLowerCase());
            return compareByWord == 0 ?
                    e1.getPartOfSpeech().compareTo(e2.getPartOfSpeech()) :
                    compareByWord;
        }
    }

    static class ComparatorByFrequency implements Comparator<DictionaryEntryOLD> {

        @Override
        public int compare(DictionaryEntryOLD o1, DictionaryEntryOLD o2) {
            return o1.getFrequency() - o2.getFrequency();
        }
    }


}
