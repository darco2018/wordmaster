package com.ust.wordmaster.service.analysing;

import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    void searchShortFormsInPredefinedSetFindsThem() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isShortFormInPredefinedSet", String.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "he's", 0));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "HE'S", 0));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "He's", 0));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "I'm", 0));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "ain't", 0));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "nonsens's", 0));

        assertFalse((boolean) method.invoke(rangeAnalyser5000, "he's", 1001));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "I'm", 1001));

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

        assertEquals("boy", method.invoke(rangeAnalyser5000, "*boy"));
        assertEquals("boy", method.invoke(rangeAnalyser5000, "(boy)"));
        assertEquals("boy", method.invoke(rangeAnalyser5000, "[(boy"));
        assertEquals("word", method.invoke(rangeAnalyser5000, "word:"));
        assertEquals("girl", method.invoke(rangeAnalyser5000, "girl?!"));
        assertEquals("girl", method.invoke(rangeAnalyser5000, "[(girl)]"));
    }


    @Test
    void returnsOutOfRangeStrings() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("LIKE", "Like", "like", "liKE", " like ",
                "He's", "he's", "HE'S",
                "boy's", "BOY'S", "Boy's",
                "girl'd", "GIRL'd", "Girl'd",
                "girl?!", "*girl", "GIRL?", "[(girl)]", "girl...",
                "are", "IS", "an", "CHILDREN", "Feet", "worst"
        );

        List<String> outOfRange = List.of("", " ", "notinDictionary");
        List<String> expected = List.of("notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(), method.invoke(rangeAnalyser5000, input, 0, 1000).toString());

    }

    @Test
    void givenNegations_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("don't", "Don't", "Hasn't", "WON'T", "ISN'T");

        List<String> outOfRange = List.of("", " ", "shan't", "notinDictionary");
        List<String> expected = List.of("shan't", "notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(), method.invoke(rangeAnalyser5000, input, 0, 1000).toString());

    }

    @Test
    void givenIrregularVerbs_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("went", "GONE", "Drove", "DRIVEN", "broke");

        List<String> outOfRange = List.of("", " ", "notinDictionary");
        List<String> expected = List.of("notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(), method.invoke(rangeAnalyser5000, input, 0, 5000).toString());
        assertEquals(List.of("Went", "TOOK").toString(), method.invoke(rangeAnalyser5000, List.of("Went", "TOOK", "burst"), 2000, 5000).toString());
    }

    @Test
    void given_S_suffix_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("books", "CATS", "Drives", "TAKES", "smiles",
                "TAXES", "Watches", "cries", "Tries");

        List<String> outOfRange = List.of("", " ", "notinDictionary");
        List<String> expected = List.of("notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(), method.invoke(rangeAnalyser5000, input, 0, 5000).toString());
        assertEquals(List.of("takes", "Tries").toString(), method.invoke(rangeAnalyser5000, List.of("takes", "Tries", "Guarantees", "BALLOONS"), 3000, 5000).toString());
    }

    @Test
    void given_ED_suffix_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("liked", "PLAYED", "Tried", "Cried", "Worked", "Placed");

        List<String> outOfRange = List.of("", " ", "notinDictionary");
        List<String> expected = List.of("notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(), method.invoke(rangeAnalyser5000, input, 0, 5000).toString());
        assertEquals(List.of("played", "Smiled").toString(), method.invoke(rangeAnalyser5000, List.of("played", "Smiled", "DIVORCED", "Eased"), 3000, 5000).toString());
    }

    @Test
    void given_ING_suffix_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("Trying", "LIKING", "smiling", "SITTING");

        List<String> outOfRange = List.of("", " ", "notinDictionary");
        List<String> expected = List.of("notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(), method.invoke(rangeAnalyser5000, input, 0, 5000).toString());
        assertEquals(List.of("played", "Smiled").toString(), method.invoke(rangeAnalyser5000, List.of("played", "Smiled", "DIVORCING", "Easing"), 3000, 5000).toString());
    }

    @Test
    void given_ER_suffix_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("smaller", "BIGGER", "Larger", "crazier");

        List<String> outOfRange = List.of("", " ", "notinDictionary");
        List<String> expected = List.of("notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(), method.invoke(rangeAnalyser5000, input, 0, 5000).toString());
        assertEquals(List.of("smaller", "BIGGER").toString(), method.invoke(rangeAnalyser5000, List.of("smaller", "BIGGER", "SEXIER", "Cuter"), 3000, 5000).toString());
    }

    @Test
    void given_EST_suffix_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("smallest", "BIGGEST", "Largest", "craziest");
        List<String> outOfRange = List.of("", " ", "notinDictionary");
        List<String> expected = List.of("notinDictionary");

        List<String> input = Stream.concat(inRange.stream(), outOfRange.stream())
                .collect(Collectors.toList());

        assertEquals(expected.toString(), method.invoke(rangeAnalyser5000, input, 0, 5000).toString());
        assertEquals(List.of("smallest", "BIGGEST").toString(), method.invoke(rangeAnalyser5000, List.of("smallest", "BIGGEST", "SEXIEST", "Cutest"), 3000, 5000).toString());
    }

    @Test
    void findsStringsWithLeadingAndTrailingSpecialChars() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_containsSpecialChars", String.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "(word"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "[(word"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "word)"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "word))"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "[(word)]"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "word:"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "word..."));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "word?!"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "-"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "*"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "%@&"));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "20bl"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "5G"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "100%"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "5"));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "he's:"));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "he's"));

        assertFalse((boolean) method.invoke(rangeAnalyser5000, "w"));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "word"));

    }

    @Test
    void searchFindsTokenAfterTransformationToBaseForm() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInRangeWhenMappedToBaseForm", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "am", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "are", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Are", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "IS", 0, 5000));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Children", 0, 5000));
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "Children", 1000, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Feet", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "wolves", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "MICE", 0, 5000));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "v", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "an", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "AN", 0, 5000));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "theatre", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "worst", 0, 5000));
    }


    @Test
    void givenNagations_searchFindsTokenAfterTransformationToVerb() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictWhenNegationMappedToBaseForm", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "isn't", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Don't", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "HASN't", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "wasn't", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "shan't", 0, 3000)); // 2217,shall
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "shan't", 0, 2000));
    }

    @Test
    void givenIrregularPastForm_searchFindsTokenAfterTransformationToBaseForm() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictWhenIrregularVerbMappedToBaseForm", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "went", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "BEEN", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Drank", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Took", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Taken", 0, 5000));
    }

    @Test
    void given_S_suffix_searchFindsToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAfterRemovingSuffix_S", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "books", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Cats", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Drinks", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "LIKES", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "JOBS", 0, 5000));

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "TAXES", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Watches", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "flies", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Cries", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "TRIES", 0, 5000));
    }

    @Test
    void given_ED_suffix_searchFindsToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAfterRemovingSuffix_ED", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "wanted", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "PLAYED", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Kicked", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Tried", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "CRIED", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "placed", 0, 5000));
    }

    @Test
    void given_ING_suffix_searchFindsToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAfterRemovingSuffix_ING", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "trying", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "LIKING", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Smiling", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Sitting", 0, 5000));
    }

    @Test
    void given_ER_suffix_searchFindsToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAfterRemovingSuffix_ER", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "smaller", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "BIGGER", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Larger", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "crazier", 0, 5000));
    }

    @Test
    void given_EST_suffix_searchFindsToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAfterRemovingSuffix_ER", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "smaller", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "BIGGER", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Larger", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "crazier", 0, 5000));
    }

    @Test
    void createsRangedText() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getRangedText", int.class, int.class, String.class, List.class);
        method.setAccessible(true);

        RangedText rangedText = (RangedText) method.invoke(rangeAnalyser5000, 0, 5000, "I like nonindictionary1 apples notindict2",
                List.of("nonindictionary1", "notindict2"));

        assertEquals(0, rangedText.getRangeStart());
        assertEquals(5000, rangedText.getRangeEnd());
        assertEquals("I like nonindictionary1 apples notindict2", rangedText.getText());
        assertEquals(new ArrayList<>(List.of("nonindictionary1", "notindict2")).toString(), rangedText.getOutOfRangeWords().toString());

    }


}
