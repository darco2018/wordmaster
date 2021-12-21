package com.ust.wordmaster.service.analysing;

import java.util.List;

/**
 * given a list of texts(paragraphs, headlines, sentences)
 */
public interface RangeAnalyser {
    List<RangedText> findOutOfRangeWords(List<String> charSequences, int dictionaryRangeStart, int dictionaryRangeEnd);
}
