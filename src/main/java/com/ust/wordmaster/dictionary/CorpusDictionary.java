package com.ust.wordmaster.dictionary;

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
public class CorpusDictionary {

    @Getter
    private String dictionaryName;

    @Getter  //  The map is sorted according to the natural ordering of its keys,
    private TreeSet<DictionaryEntry> dictionary;

    public CorpusDictionary(String name, Collection<DictionaryEntry> entries) {
        Objects.requireNonNull(name, "Dictionary name cannot be null");
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Dictionary name cannot be empty or blank");
        }

        Objects.requireNonNull(entries, "Dictionary cannot be null");

        this.dictionaryName = name;

        this.dictionary = new TreeSet<>();
        for (DictionaryEntry e : entries) {
            this.dictionary.add(
                    new DictionaryEntry(e.getWord(), e.getRank(), e.getPartOfSpeech(), e.getFrequency(), e.getDispersion())
            );
        }
    }

    public NavigableSet<DictionaryEntry> getDictionaryByRank(boolean reversed) {

        return Collections.unmodifiableNavigableSet(new TreeSet<>(reversed ? dictionary.descendingSet() : dictionary));
    }

    public NavigableSet<DictionaryEntry> getDictionaryByWord(boolean reversed) {

        ComparatorByWord byWord = new ComparatorByWord();
        NavigableSet<DictionaryEntry> sortedByWord = new TreeSet<>(reversed ? byWord.reversed() : byWord);
        sortedByWord.addAll(dictionary);

        return Collections.unmodifiableNavigableSet(sortedByWord);
    }

    // checks if contains word but maybe it's a word, maybe it's a noun
    // we don't have O(1) for word lookup
    public boolean containsWord(final String word, final int rangeStart, final int rangeEnd) {

        validate(word, rangeStart, rangeEnd);

        String wordFixed = word.toLowerCase();
        wordFixed = replace(wordFixed);

        NavigableSet<DictionaryEntry> rangeDictionary = getDictionarySubset(rangeStart, rangeEnd);

        for (DictionaryEntry entry : rangeDictionary) {
            if (entry.getWord().equalsIgnoreCase(wordFixed)) {
                return true;
            }
        }

        return false;
    }

    private void validate(String word, int rangeStart, int rangeEnd) {
        if(word == null){
            throw new NullPointerException("Word cannot be null");
        }

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

    private NavigableSet<DictionaryEntry> getDictionarySubset(int rangeStart, int rangeEnd) {

        DictionaryEntry startEntry = null;
        DictionaryEntry endEntry = null;
        for (DictionaryEntry entry : this.dictionary) {
            if (entry.getRank() == rangeStart) {
                startEntry = entry;
            } else if (entry.getRank() == rangeEnd) {
                endEntry = entry;
            }

            if (startEntry != null && endEntry != null) {
                break;
            }
        }

        NavigableSet<DictionaryEntry> rangeDictionary = this.dictionary.subSet(
                startEntry, true, endEntry, true);
        return rangeDictionary;
    }

    private String replace(String word) {
        word = word.toLowerCase();
        HashMap<String, String> replacementMap = new HashMap<>();
        replacementMap.put("am", "be");
        replacementMap.put("are", "be");
        replacementMap.put("is", "be");
        replacementMap.put("was", "be");
        replacementMap.put("were", "be");
        replacementMap.put("has", "have");
        replacementMap.put("had", "have");

        replacementMap.put("an", "a");

        replacementMap.put("aren't", "n't");
        replacementMap.put("isn't", "n't");
        replacementMap.put("don't", "n't");
        replacementMap.put("doesn't", "n't");
        replacementMap.put("wasn't", "n't");
        replacementMap.put("weren't", "n't");
        replacementMap.put("haven't", "n't");
        replacementMap.put("hasn't", "n't");
        replacementMap.put("hadn't", "n't");
        replacementMap.put("won't", "n't");
        replacementMap.put("wouldn't", "n't");
        replacementMap.put("can't", "n't");
        replacementMap.put("couldn't", "n't");
        replacementMap.put("shan't", "n't");
        replacementMap.put("shouldn't", "n't");

        replacementMap.put("children", "child");
        replacementMap.put("grandchildren", "grandchild");
        replacementMap.put("mice", "mouse");
        replacementMap.put("wives", "wife");
        replacementMap.put("wolves", "wolf");
        replacementMap.put("knives", "knife");
        replacementMap.put("halves", "half");
        replacementMap.put("selves", "self");
        replacementMap.put("feet", "foot");
        replacementMap.put("teeth", "tooth");
        replacementMap.put("men", "man");
        replacementMap.put("women", "woman");
        replacementMap.put("lying", "lie");

        replacementMap.put("metre", "meter");
        replacementMap.put("theatre", "theater");

        return replacementMap.getOrDefault(word, word);
    }

    static class ComparatorByWord implements Comparator<DictionaryEntry> {

        @Override
        public int compare(DictionaryEntry e1, DictionaryEntry e2) {
            int compareByWord = e1.getWord().toLowerCase().compareTo(e2.getWord().toLowerCase());
            return compareByWord == 0 ?
                    e1.getPartOfSpeech().compareTo(e2.getPartOfSpeech()) :
                    compareByWord;
        }
    }

    static class ComparatorByFrequency implements Comparator<DictionaryEntry> {

        @Override
        public int compare(DictionaryEntry o1, DictionaryEntry o2) {
            return o1.getFrequency() - o2.getFrequency();
        }
    }

}
