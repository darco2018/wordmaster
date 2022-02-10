package com.ust.wordmaster.service.range;

import lombok.ToString;

import java.util.Objects;

/**
 * Encapsulates the data passed to a range analyser (the original text,
 * the range start and end) plus the data produced by the range analyser
 * (words outside the given range)
 */
@ToString
public class RangedText5000 implements RangedText {

    private final String text;
    private final int rangeStart;
    private final int rangeEnd;
    private String[] outOfRangeWords;

    public RangedText5000(String text, int rangeStart, int rangeEnd) {
        Objects.requireNonNull(text, "Text cannot be null");
        this.text = text;

        if (rangeStart < 0 || rangeStart >= rangeEnd)
            throw new IllegalArgumentException("Range start must be greater than 0 and less than range end.");
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public int getRangeStart() {
        return this.rangeStart;
    }

    @Override
    public int getRangeEnd() {
        return this.rangeEnd;
    }

    @Override
    public String[] getOutOfRangeWords() {
        return this.outOfRangeWords;
    }

    @Override
    public void setOutOfRangeWords(String[] outOfRangeWords) {
        Objects.requireNonNull(outOfRangeWords, "Out of range words cannot be null");
        this.outOfRangeWords = outOfRangeWords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RangedText5000 that = (RangedText5000) o;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
