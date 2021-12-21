package com.ust.wordmaster.service.analysing;

import lombok.ToString;

import java.util.Objects;

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
}
