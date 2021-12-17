package com.ust.wordmaster.service.filteringOLD;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TextUnitOLD {

    private String text;
    private String[] words;

    public TextUnitOLD(TextUnitOLD headline) {
        this.text = headline.getText();
        this.words = headline.getWords();
    }
}
