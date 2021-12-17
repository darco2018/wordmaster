package com.ust.wordmaster.newdictionary;

import java.util.List;

public interface TextUnitCreator {
    List<TextUnit> parseIntoTextUnits(List<String> charSequences, int dictionaryRangeStart, int dictionaryRangeEnd);
}
