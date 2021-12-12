package com.ust.wordmaster.service.filtering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Headline {

    private String headline;
    private String[] words;

    public Headline(Headline headline) {
        this.headline = headline.getHeadline();
        this.words = headline.getWords();
    }
}
