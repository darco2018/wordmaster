package com.ust.wordmaster.newdictionary;

import java.util.List;

/**
 * given a list of texts(paragraphs, headlines, sentences)
 */
public interface RangeAnalyser {
    List<RangedText> analyseRange(List<String> charSequences, int dictionaryRangeStart, int dictionaryRangeEnd);
}
