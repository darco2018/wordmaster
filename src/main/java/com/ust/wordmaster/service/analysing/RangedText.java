package com.ust.wordmaster.service.analysing;

public interface RangedText {

    String getText();
    String[] getOutOfRangeWords();
    void setOutOfRangeWords(String[] outOfRangeWords);
    int getRangeStart();
    int getRangeEnd();

}
