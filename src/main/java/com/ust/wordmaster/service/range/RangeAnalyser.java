package com.ust.wordmaster.service.range;

import java.util.List;

/**
 * Finds words out of range for each character sequence in the list
 */
public interface RangeAnalyser {
    List<RangedText> findOutOfRangeWords(List<String> charSequences, int dictionaryRangeStart, int dictionaryRangeEnd);
}
