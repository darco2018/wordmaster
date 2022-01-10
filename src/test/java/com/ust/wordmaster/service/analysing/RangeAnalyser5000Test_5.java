package com.ust.wordmaster.service.analysing;

import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RangeAnalyser5000Test_5 {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    private static CorpusDictionary5000 corpusDictionary; // tests specific for CorpusDictionary5000

    @BeforeAll
    static void setUp() {
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);
    }

    @Test
    void searchShortFormsInPredefinedSetFindsThemTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isShortFormInPredefinedSet", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "he's", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "HE'S", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "He's", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "I'm", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "ain't", 0, 1000));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "nonsens's", 0, 1000));

        assertFalse((boolean) method.invoke(rangeAnalyser5000, "he's", 1001, 2000));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "I'm", 1001, 5000));

    }

    @Test
    void searchShortFormsFindsThem() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAfterRemovingSuffixes_d_s_ll", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "boy's", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "BOY'S", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Boy's", 0, 1000));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "boy's", 1000, 5000));


        assertTrue((boolean) method.invoke(rangeAnalyser5000, "girl'd", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "GIRL'd", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Girl'd", 0, 1000));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "girl'd", 1000, 5000));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "cat'll", 1000, 2000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Cat'll", 1000, 2000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "CAT'll", 1000, 2000));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "cat'll", 0, 999));
    }

    @Test
    void removesNonLetterChars() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_removeLeadingTrailingSpecialChars", String.class);
        method.setAccessible(true);

        assertEquals("boy", (String) method.invoke(rangeAnalyser5000, "*boy"));
        assertEquals("boy", (String) method.invoke(rangeAnalyser5000, "(boy)"));
        assertEquals("boy", (String) method.invoke(rangeAnalyser5000, "[(boy"));
        assertEquals("word", (String) method.invoke(rangeAnalyser5000, "word:"));
        assertEquals("girl", (String) method.invoke(rangeAnalyser5000, "girl?!"));
        assertEquals("girl", (String) method.invoke(rangeAnalyser5000, "[(girl)]"));
    }

    @Test
    void searchFindsAfterRemovingNonLetterChars() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAfterRemovingLeadingAndTrailingSpecialChars", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "(boy)", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "BOY?!", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "*Boy", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "[(boy)]", 0, 1000));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "[(boy)]", 1000, 5000));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "girl...", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "GIRL+", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "#Girl", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "@girl.", 0, 1000));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "@girl.", 1000, 5000));

    }

    @Test
    void returnsOutOfRangeStrings() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of( "LIKE", "Like", "like", "liKE", " like ",
                                        "He's", "he's", "HE'S",
                                        "boy's", "BOY'S", "Boy's",
                                        "girl'd", "GIRL'd", "Girl'd",
                                        "girl?!", "*girl", "GIRL?", "[(girl)]", "girl..."
                );

        List<String> outOfRange = List.of("", " ", "notinDictionary");
        List<String> expected = List.of("notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(),    method.invoke(rangeAnalyser5000, input, 0, 1000).toString());

    }


}
