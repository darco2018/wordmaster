package com.ust.wordmaster.newdictionary;

public interface RangedText {

    String getText();
    String[] getOutOfRangeWords();
    int getRangeStart();
    int getRangeEnd();

}
