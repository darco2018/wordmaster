package com.ust.wordmaster.newdictionary;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RangeAnalyser5000Test {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    private static CorpusDictionary corpusDictionary;



    @BeforeAll
    static void setUp() {
        List<DictionaryEntry> entriesFromFile = CSVParser.parse(DICTIONARY_FILE);
        corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);
    }

    @Test
    void givenSimpleInputAnd0_5000Range_findOutOfRange_findsOutOfRangeWords() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText = "like sport and outofrangeword anotherNotInDictionary";
        actAndAssert(rangeAnalyser5000, inputText);

    }

    @Test
    void givenShortFormsAnd0_5000Range_analyseRange_findsThemInRange() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText = "they'll smile he's she's outofrangeword I'm anotherNotInDictionary";

        actAndAssert(rangeAnalyser5000, inputText);
    }

    private void actAndAssert(RangeAnalyser5000 rangeAnalyser5000, String inputText) {
        List<String> charSequences = List.of(inputText);
        List<RangedText> rangedTextList = rangeAnalyser5000.findOutOfRange(charSequences, 0, 5000);

        assertEquals(1, rangedTextList.size());

        RangedText rangedText = rangedTextList.get(0);
        assertEquals(inputText, rangedText.getText());
        assertEquals(0, rangedText.getRangeStart());
        assertEquals(5000, rangedText.getRangeEnd());
        assertEquals(2, rangedText.getOutOfRangeWords().length);
        assertEquals("outofrangeword", rangedText.getOutOfRangeWords()[0]);
        assertEquals("anotherNotInDictionary", rangedText.getOutOfRangeWords()[1]);
    }

    @Test
    void givenSNegationsAnd0_5000Range_analyseRange_findsThemInRange() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText = "outofrangeword don't isn't aren't anotherNotInDictionary hasn't ";
        actAndAssert(rangeAnalyser5000, inputText);
    }

    @Test
    void givenInflectedFormsAnd0_5000Range_analyseRange_findsThemInRange() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText = "am is was outofrangeword had an anotherNotInDictionary";
        actAndAssert(rangeAnalyser5000, inputText);
    }

    @Test
    void givenIrreglarPluralFormsAnd0_5000Range_analyseRange_findsThemInRange() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText = "women men outofrangeword children  mice anotherNotInDictionary feet teeth ";
        actAndAssert(rangeAnalyser5000, inputText);
    }

    @Test
    void givenRegularPluralAndPossesivesAnd0_5000Range_analyseRange_findsThemInRange() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText = "boy's boys outofrangeword cats  cat's cats' anotherNotInDictionary children's animals ";
        actAndAssert(rangeAnalyser5000, inputText);
    }

    @Test
    void givenPastSimpleAndINGandPresentSimpleS_And0_5000Range_analyseRange_findsThemInRange() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText = "smiled walked tried plays cries ties outofrangeword  anotherNotInDictionary trying liking talking";
        actAndAssert(rangeAnalyser5000, inputText);
    }

    @Test
    void givenSuperlatives_And0_5000Range_analyseRange_findsThemInRange() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String inputText = "smallest largest worst best outofrangeword  anotherNotInDictionary";


        actAndAssert(rangeAnalyser5000, inputText);
    }


}