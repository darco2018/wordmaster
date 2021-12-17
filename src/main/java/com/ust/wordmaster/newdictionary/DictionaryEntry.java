package com.ust.wordmaster.newdictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@EqualsAndHashCode
@ToString
public abstract class DictionaryEntry implements Comparable<DictionaryEntry> {

    @Getter
    private final String word;

    @Getter
    private WordData wordData;

    public DictionaryEntry(String word) {
        Objects.requireNonNull(word, "The word cannot be null.");
        if (word.isEmpty() || word.isBlank())
            throw new IllegalArgumentException("The word cannot be empty or blank");

        this.word = word;
    }

    public void setWordData(WordData wordData) {
        Objects.requireNonNull(word, "The word cannot be null.");
        this.wordData = wordData;
    }

    @Override
    public int compareTo(DictionaryEntry o) {
        Objects.requireNonNull(o, "DictionaryEntry cannot be null.");
        return this.word.compareTo(o.getWord());
    }
}
