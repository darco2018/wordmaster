package com.ust.wordmaster.service.range;

import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeAnalyser5000Test_1_isInRange {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    private static CorpusDictionary5000 corpusDictionary; // tests specific for CorpusDictionary5000

    @BeforeAll
    static void setUp() {
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);
    }

    @Test
    void givenSearchOptionCaseUnchanged_whenSearchInDictionary_findsCorrectly() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInRange", String.class, int.class, int.class, RangeAnalyser5000.SearchOption.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "want", 0, 1000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "eat", 0, 1000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "want", 1001, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "eat", 1001, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));

        // weather - rank 1623
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "weather", 0, 1000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "weather", 1001, 2000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "weather", 2000, 3000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "weather", 2000, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));

        // profound - rank 4771
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "offender", 0, 4770, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "offender", 0, 4771, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "offender", 4771, 4772, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "offender", 4772, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "offender", 2000, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));

        // Christmas, CEO, like
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Christmas", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "christmas", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "CHRISTMAS", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "CEO", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "ceo", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "ceo", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "like", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "LIKE", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "Like", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "liKE", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));

        // Mr - only
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Mr", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "MR", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "mr", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "mR", 0, 5000, RangeAnalyser5000.SearchOption.CASE_UNCHANGED));
    }

    @Test
    void givenSearchOptionAllCases__whenSearchInDictionary_findsCorrectly() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInRange", String.class, int.class, int.class, RangeAnalyser5000.SearchOption.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "want", 0, 1000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "eat", 0, 1000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "want", 1001, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "eat", 1001, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));

        // weather - rank 1623
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "weather", 0, 1000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "weather", 1001, 2000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "weather", 2000, 3000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "weather", 2000, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));

        // profound - rank 4771
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "offender", 0, 4770, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "offender", 0, 4771, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "offender", 4771, 4772, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "offender", 4772, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "offender", 2000, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));

        //Forms present in dictionary: Mr, Christmas, CEO, like
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Mr", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "MR", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "mr", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "mR", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "christmas", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "CHRISTMAS", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Christmas", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "ceo", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "LIKE", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Like", 0, 5000, RangeAnalyser5000.SearchOption.CASE_ALL));

    }




}