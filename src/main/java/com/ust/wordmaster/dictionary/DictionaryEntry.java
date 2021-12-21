package com.ust.wordmaster.dictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@EqualsAndHashCode
@ToString
public abstract class DictionaryEntry implements Comparable<DictionaryEntry> {

    @Getter
    private final String headword;

    @Getter
    private WordData wordData;

    public DictionaryEntry(String headword) {
        Objects.requireNonNull(headword, "The word cannot be null.");
        if (headword.isEmpty() || headword.isBlank())
            throw new IllegalArgumentException("The word cannot be empty or blank");
        else
            this.headword = headword;
    }

    public DictionaryEntry(String headword, WordData wordData) {
        Objects.requireNonNull(headword, "The word cannot be null.");
        if (headword.isEmpty() || headword.isBlank())
            throw new IllegalArgumentException("The word cannot be empty or blank");
        else
            this.headword = headword;

        Objects.requireNonNull(wordData, "The word data cannot be null.");
        this.wordData = wordData;
    }

    @Override
    public int compareTo(DictionaryEntry o) {
        Objects.requireNonNull(o, "DictionaryEntry cannot be null.");
        return this.headword.compareTo(o.getHeadword());
    }
}
