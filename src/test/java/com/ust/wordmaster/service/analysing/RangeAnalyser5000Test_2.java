package com.ust.wordmaster.service.analysing;

import com.ust.wordmaster.dictionary.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// test  1)for DIFFERENT ranges 0-5000 2) List<String> charSequences = List.of(inputText) containing a MULTIPLE items
// 3) private methods
class RangeAnalyser5000Test_2 {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    private static CorpusDictionary corpusDictionary;

    @BeforeAll
    static void setUp() {
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);
    }

    // test private method
    @Test
    void findsOutOfRangeIndexes() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = RangeAnalyser5000.class.getDeclaredMethod("isolateOutOfRangeWords", String[].class, int.class, int.class);
        method.setAccessible(true);
        String inputText = "outofrangeword_0 dogs like aren't anotherNotInDictionary_4 she's ";
        String[] words = inputText.split(" ");

                                //invoke() returns Object
        int[] notInRange = (int[]) method.invoke(rangeAnalyser5000, words, 1, 5000);

        assertArrayEquals(new int[]{0,4}, notInRange);
    }

    // test private method
    @Test
    void convertsIndexesToWords() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = RangeAnalyser5000.class.getDeclaredMethod("convertIndexesToWords", int[].class, String[].class);
        method.setAccessible(true);
        String inputText = "outofrangeword_0 dogs like aren't anotherNotInDictionary_4 she's ";
        String[] words = inputText.split(" ");
        int[] indexesOutOfRange = {0,4};

        //invoke() returns Object
        String[] wordsNotInRange = (String[]) method.invoke(rangeAnalyser5000, indexesOutOfRange, words);

        assertArrayEquals(new String[]{"outofrangeword_0", "anotherNotInDictionary_4"}, wordsNotInRange);
    }

    @Test
    void givenSimpleInputAnd0_5000Range_findOutOfRangeWords_findsOutOfRangeWords() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText_1 = "like sport and strangeword whatsthisstupidword";
        String inputText_2 = "they'll smile he's she's strangeword I'm whatsthisstupidword";
        String inputText_3 = "strangeword don't isn't aren't whatsthisstupidword hasn't ";

        List<String> charSequences = List.of(inputText_1, inputText_2, inputText_3);
        List<RangedText> rangedTextList = rangeAnalyser5000.findOutOfRangeWords(charSequences, 0, 5000);

        assertEquals(3, rangedTextList.size());

        assertRangedText(inputText_1, rangedTextList, 0);
        assertRangedText(inputText_2, rangedTextList, 1);
        assertRangedText(inputText_3, rangedTextList, 2);
    }

    private void assertRangedText(String inputText, List<RangedText> rangedTextList, int index) {

        RangedText rangedText = rangedTextList.get(index);
        assertEquals(inputText, rangedText.getText());
        assertEquals(0, rangedText.getRangeStart());
        assertEquals(5000, rangedText.getRangeEnd());
        assertEquals(2, rangedText.getOutOfRangeWords().length);
        assertEquals("strangeword", rangedText.getOutOfRangeWords()[0]);
        assertEquals("whatsthisstupidword", rangedText.getOutOfRangeWords()[1]);
    }

    @Test
    void given0_1000Range_findOutOfRangeWords_findsOutOfRangeWords() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String volume_1931rank = "volume";
        String concede_4232 = "concede";
        String inputText = " mind " + volume_1931rank + " face " + concede_4232 + " fight ";
        int[] range = new int[]{0, 1000};

        actAndAssert(rangeAnalyser5000, inputText, range, volume_1931rank, concede_4232);
    }

    @Test
    void given1700_4500Range_findOutOfRangeWords_findsOutOfRangeWords() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String volume_1931rank = "volume";
        String concede_4232 = "concede";
        // voice = 466 rank,  vehicle 1300  ,influential 4334
        String inputText = " voice " + volume_1931rank + " vehicle " + concede_4232 + " influential ";
        int[] range = new int[]{1700, 4500};

        actAndAssert(rangeAnalyser5000, inputText, range, "voice", "vehicle", "influential");
    }

    @Test
    void given0_4000Range_findOutOfRangeWords_findsOutOfRangeWords() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String volume_1931rank = "volume";
        String concede_4232 = "concede";
        // voice = 466 rank,  vehicle 1300  ,influential 4334
        String inputText = " voice " + volume_1931rank + " vehicle " + concede_4232 + " influential ";
        int[] range = new int[]{0, 4000};

        actAndAssert(rangeAnalyser5000, inputText, range, concede_4232, "influential");
    }

    private void actAndAssert(RangeAnalyser5000 rangeAnalyser5000, String inputText, int[] range, String... outOfRange) {
        List<String> charSequences = List.of(inputText);
        int rangeStart = range[0];
        int rangeEnd = range[1];
        List<RangedText> rangedTextList = rangeAnalyser5000.findOutOfRangeWords(charSequences, rangeStart, rangeEnd);

        assertEquals(1, rangedTextList.size());


        RangedText rangedText = rangedTextList.get(0);
        assertEquals(inputText, rangedText.getText());
        assertEquals(rangeStart, rangedText.getRangeStart());
        assertEquals(rangeEnd, rangedText.getRangeEnd());
        assertEquals(2, rangedText.getOutOfRangeWords().length);
        assertEquals(outOfRange[0], rangedText.getOutOfRangeWords()[0]);
        assertEquals(outOfRange[1], rangedText.getOutOfRangeWords()[1]);
    }

}