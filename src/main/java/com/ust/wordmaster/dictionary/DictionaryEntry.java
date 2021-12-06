package com.ust.wordmaster.dictionary;

import lombok.Data;

import java.util.Objects;


/**
 * Represents a single entry in a dictionary
 */
@Data
public class DictionaryEntry {
    /**
     * The word's rank based on the frequency of occurrence. The more frequent the word the higher th rank
     */
    private int rank;
    private String word;
    private String partOfSpeech;
    /**
     * how many times the word appears in the corpus
     */
    private int frequency;
    /**
     * the degree to which occurrences of a word are distributed throughout a corpus evenly or unevenly/clumpily
     * https://www.researchgate.net/publication/332120488_Analyzing_dispersion
     */
    private float dispersion;

    /**
     * Two words are equal if they have the same word and part of speech
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryEntry that = (DictionaryEntry) o;
        return word.equals(that.word) &&
                partOfSpeech.equals(that.partOfSpeech);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, partOfSpeech);
    }
}
