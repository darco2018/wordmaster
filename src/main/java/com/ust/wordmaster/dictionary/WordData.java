package com.ust.wordmaster.dictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@EqualsAndHashCode
@ToString
/**
 * Represents the data connected with the word itself
 */
public final class WordData {
    /**
     * The word's rank based on the frequency of occurrence. The more frequent the word the higher th rank
     */
    @Setter private  int rank;
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

    public WordData(final int rank,
                    final String partOfSpeech,
                    final int frequency,
                    final double dispersion) {
        Objects.requireNonNull(partOfSpeech, "Part of speech cannot be null");
        if (rank < 1)
            throw new IllegalArgumentException("Rank cannot be less than 1");

        if (frequency < 1)
            throw new IllegalArgumentException("Frequency cannot be less than 1");

        if (dispersion < 0 || dispersion > 100)
            throw new IllegalArgumentException("Dispersion must be int the range 0-100");

        this.rank = rank;
        this.partOfSpeech = partOfSpeech;
        this.frequency = frequency;
        this.dispersion = dispersion;
    }
}
