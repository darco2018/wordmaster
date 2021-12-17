package com.ust.wordmaster.service.filtering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TextUnit {

    private String text;
    private String[] words;

    public TextUnit(TextUnit headline) {
        this.text = headline.getText();
        this.words = headline.getWords();
    }
}
