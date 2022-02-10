package com.ust.wordmaster.exercise;

import com.ust.wordmaster.service.range.RangedText;

import java.util.List;

public class ExerciseMaker {

    public List<RangedText> removeIfNoOutOfRangeWords(List<RangedText> rangedTexts) {

        return rangedTexts.stream()
                .filter(rangedText -> rangedText.getOutOfRangeWords().length > 0)
                .toList();
    }
}
