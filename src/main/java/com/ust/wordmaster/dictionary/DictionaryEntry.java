package com.ust.wordmaster.dictionary;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * Immutable dictionary created only once to store words from corpus data with word data such as
 * rank, frequency, etc
 */
@Getter
@ToString
public final class DictionaryEntry implements Comparable<DictionaryEntry> {

    private final WordRoot wordRoot;
    private final WordData wordData;

    public DictionaryEntry(final WordRoot wordRoot, final WordData wordData) {
        Objects.requireNonNull(wordRoot, "WordRoot cannot be null");
        Objects.requireNonNull(wordData, "WordData cannot be null");

        this.wordRoot = wordRoot;
        this.wordData = wordData;
    }

    /**
     * Two entries are equal if they have the same root and the same part of speech
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryEntry entry = (DictionaryEntry) o;
        return Objects.equals(wordRoot, entry.wordRoot) &&
                Objects.equals(wordData.getPartOfSpeech(), entry.wordData.getPartOfSpeech());
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordRoot, wordData.getPartOfSpeech());
    }

    /**
     * This object is greater if it has a greater root (eg 'dance' goes before 'eat'). If the roots are equal,
     * the psrt of speech is compared, so that 'dance' 'n' goes before 'dance' 'v'
     */
    @Override
    public int compareTo(DictionaryEntry o) {
        int rootComparison = this.wordRoot.getWord().compareTo(o.wordRoot.getWord());
        return rootComparison == 0 ?
                this.wordData.getPartOfSpeech().compareTo(o.wordData.getPartOfSpeech()) :
                rootComparison;
    }


}
