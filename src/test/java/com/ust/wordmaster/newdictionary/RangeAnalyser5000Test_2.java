package com.ust.wordmaster.newdictionary;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// test  for DIFFERENT ranges 0-5000 and  List<String> charSequences = List.of(inputText) containing a MULTIPLE items
class RangeAnalyser5000Test_2 {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    private static CorpusDictionary corpusDictionary;

    @BeforeAll
    static void setUp() {
        List<DictionaryEntry> entriesFromFile = CSVParser.parse(DICTIONARY_FILE);
        corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);
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