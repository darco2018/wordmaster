package com.ust.wordmaster.dictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@EqualsAndHashCode
@ToString
/**
 * Represents the data connected with the word itself
 */
public final class DictionaryEntry implements Comparable<DictionaryEntry> {
    private final String partOfSpeech;
    /**
     * frequency - how many times the word appears in the corpus
     */
    private final int frequency;
    /**
     * dispersion -the degree to which occurrences of a word are distributed throughout a corpus evenly or unevenly/clumpily
     * https://www.researchgate.net/publication/332120488_Analyzing_dispersion
     */
    private final double dispersion;
    /**
     * The word's rank based on the frequency of occurrence. The more frequent the word the higher th rank
     */
    private int rank;
    private String word;

    public DictionaryEntry(final String word,
                           final int rank,
                           final String partOfSpeech,
                           final int frequency,
                           final double dispersion) {

        Objects.requireNonNull(word, "Word cannot be null");
        if (word.isBlank() || word.isEmpty()) {
            throw new IllegalArgumentException("Word cannot be empty or blank");
        }

        Objects.requireNonNull(partOfSpeech, "Part of speech cannot be null");
        if (rank < 1)
            throw new IllegalArgumentException("Rank cannot be less than 1");

        if (frequency < 1)
            throw new IllegalArgumentException("Frequency cannot be less than 1");

        if (dispersion < 0 || dispersion > 100)
            throw new IllegalArgumentException("Dispersion must be int the range 0-100");

        this.word = word;
        this.rank = rank;
        this.partOfSpeech = partOfSpeech;
        this.frequency = frequency;
        this.dispersion = dispersion;
    }

    @Override
    public int compareTo(DictionaryEntry o) {
        return this.rank - o.rank;
    }
}
