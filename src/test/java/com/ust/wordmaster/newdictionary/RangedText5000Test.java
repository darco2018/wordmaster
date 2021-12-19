package com.ust.wordmaster.newdictionary;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RangedText5000Test {

    private static String text = "I like stupendous ravioli and music";
    private static int rangeStart = 0;
    private static int rangeEnd = 5000;
    private static String[] outOfRangeWords = new String[]{"stupendous", "ravioli"};
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