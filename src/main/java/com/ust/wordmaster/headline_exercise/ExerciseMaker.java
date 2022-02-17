package com.ust.wordmaster.headline_exercise;

import com.ust.wordmaster.service.range.RangedText;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExerciseMaker {

    public List<RangedText> getTextsWithNonZeroNumberOutOfRangeWords(List<RangedText> rangedTexts) {

        return rangedTexts.stream()
                .filter(rangedText -> rangedText.getOutOfRangeWords().length > 0)
                .toList();
    }

    public HeadlineExercise createExercise(final List<RangedText> rangedTexts, final String blankString, final String answersSeparator, final int quizItemsLimit) {

        int outOfRangeWordsTotal = 0;
        String content = "";

        for (RangedText rangedText : getTextsWithNonZeroNumberOutOfRangeWords(rangedTexts)) {
            outOfRangeWordsTotal += rangedText.getOutOfRangeWords().length;
            if (outOfRangeWordsTotal > quizItemsLimit) {
                break;
            }
            content += getLine(blankString, answersSeparator, rangedText) + "\n";
        }

        content = content.substring(0, content.length() - 1); // remove last \n

        HeadlineExercise headlineExercise = new HeadlineExercise();
        headlineExercise.setContent(content);

        return headlineExercise;
    }

    private String getLine(final String blankString, final String answersSeparator, final RangedText rangedText) {

        String[] answers = rangedText.getOutOfRangeWords();
        String withReplacements = replaceAnswersWithGaps(rangedText.getText(), answers, blankString);

        return withReplacements + answersSeparator + getAnswersString(answers);

    }

    // [lion, jungle]
    private String replaceAnswersWithGaps(final String text, final String[] answers, final String replacement) {
        String textWithGaps = text;
        for (String answer : answers) {
            textWithGaps = textWithGaps.replace(answer, replacement);
        }
        return textWithGaps;
        // "The <?> lives in the <?>.";
    }

    private String getAnswersString(String[] answers) {
        return Arrays.stream(answers).collect(Collectors.joining(" "));
        // return "lion jungle";
    }
}
