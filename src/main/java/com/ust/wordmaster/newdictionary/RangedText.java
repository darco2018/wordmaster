package com.ust.wordmaster.newdictionary;

public interface RangedText {

    String getText();
    String[] getOutOfRangeWords();
    void setOutOfRangeWords(String[] outOfRangeWords);
    int getRangeStart();
    int getRangeEnd();

}
