package com.ust.wordmaster.service.range;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RangedText5000Test {

    private static final String text = "I like stupendous ravioli and music";
    private static final int rangeStart = 0;
    private static final int rangeEnd = 5000;
    private static final String[] outOfRangeWords = new String[]{"stupendous", "ravioli"};
    private static RangedText rangedText;

    @BeforeAll
    static void setUp() {
        rangedText = new RangedText5000(text, rangeStart, rangeEnd);
        rangedText.setOutOfRangeWords(outOfRangeWords);
    }


    @Test
    void getters_returnCorrectValue() {

        assertEquals(text, rangedText.getText());
        assertEquals(rangeStart, rangedText.getRangeStart());
        assertEquals(rangeEnd, rangedText.getRangeEnd());
        assertEquals(outOfRangeWords, rangedText.getOutOfRangeWords());

    }

    @Test
    void givenInvalidRangeStartAndRangeEnd_constructor_throwExceptions() {
        assertThrows(IllegalArgumentException.class, () -> new RangedText5000(text, -1, 1000));
        assertThrows(IllegalArgumentException.class, () -> new RangedText5000(text, 50, 50));
        assertThrows(IllegalArgumentException.class, () -> new RangedText5000(text, 101, 100));




    }


}