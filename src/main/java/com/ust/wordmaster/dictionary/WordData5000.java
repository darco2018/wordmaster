package com.ust.wordmaster.dictionary;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class WordData5000 implements WordData, Comparable<WordData5000> {

    private String word;

    private String partOfSpeech;
    /**
     * frequency - how many times the word appears in the corpus
     */
    private int frequency;
    /**
     * dispersion -the degree to which occurrences of a word are distributed throughout a corpus evenly or unevenly/clumpily
     * https://www.researchgate.net/publication/332120488_Analyzing_dispersion
     */
    private double dispersion;
    /**
     * The word's rank based on the frequency of occurrence. The more frequent the word the higher th rank
     */
    private int rank;

    public WordData5000() {
    }

    public WordData5000(
            final String word,
            final int rank,
            final String partOfSpeech,
            final int frequency,
            final double dispersion) {

        Objects.requireNonNull(word, "The word cannot be null.");
        if (word.isEmpty() || word.isBlank())
            throw new IllegalArgumentException("The word cannot be empty or blank");

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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WordData5000 that = (WordData5000) o;
        return word.equals(that.word) && partOfSpeech.equals(that.partOfSpeech);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, partOfSpeech);
    }

    @Override
    public int compareTo(WordData5000 o) {
        int byWord = this.word.compareTo(o.word);
        return byWord == 0 ? Integer.compare(this.rank, o.rank) : byWord;
    }
}
