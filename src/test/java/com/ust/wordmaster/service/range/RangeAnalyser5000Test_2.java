package com.ust.wordmaster.service.range;

import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RangeAnalyser5000Test_2 {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    private static CorpusDictionary5000 corpusDictionary; // tests specific for CorpusDictionary5000

    @BeforeAll
    static void setUp() {
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);
    }

    @Test
    void givenShortForms_searchInPredefinedSet_FindsThem() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
    void givenSuffixesD_S_LL_searchFindsThem() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAfterRemovingSuffixes_d_s_ll", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "boy's", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "BOY'S", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Boy's", 0, 1000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "Country's", 0, 1000));
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
    void removesLeadingTrailingNonLetterChars() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_removeLeadingTrailingSpecialChars", String.class);
        method.setAccessible(true);

        assertEquals("boy", method.invoke(rangeAnalyser5000, "*boy"));
        assertEquals("boy", method.invoke(rangeAnalyser5000, "(boy)"));
        assertEquals("boy", method.invoke(rangeAnalyser5000, "[(boy"));
        assertEquals("word", method.invoke(rangeAnalyser5000, "word:"));
        assertEquals("girl", method.invoke(rangeAnalyser5000, "girl?!"));
        assertEquals("girl", method.invoke(rangeAnalyser5000, "[(girl)]"));
        assertEquals("he's", method.invoke(rangeAnalyser5000, "he's"));
        assertEquals("I'm", method.invoke(rangeAnalyser5000, "I'm"));
        assertEquals("THEY'RE", method.invoke(rangeAnalyser5000, "THEY'RE"));
        assertEquals("books", method.invoke(rangeAnalyser5000, "books'"));
    }

    @Test
    void givenListOfTokens_searchInDictionary_findsOutOfRangeStrings() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_getOutOfRangeStrings", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> inRange = List.of("LIKE", "Like", "like", "liKE", " like ",
                "He's", "he's", "HE'S",
                "boy's", "BOY'S", "Boy's",
                "Country's",
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
    void givenNegations_searchInDictionary_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
    void givenIrregularVerbs_searchInDictionary_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
    void given_S_suffix_searchInDictionary_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
    void given_ED_suffix_searchInDictionary_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
    void given_ING_suffix_searchInDictionary_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
    void given_ER_suffix_searchInDictionary_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
    void given_EST_suffix_searchInDictionary_findsThemInRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
        assertFalse((boolean) method.invoke(rangeAnalyser5000, "shan't", 0, 5000)); // exception: shan't is not converted to shall
    }

    @Test
    void givenIrregularPastForm__searchInDictionary_findsTokenAfterTransformationToBaseForm() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
    void given_S_suffix_whenSuffixRemoved_searchFindsToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
        assertEquals(new ArrayList<>(List.of("nonindictionary1", "notindict2")).toString(), Arrays.toString(rangedText.getOutOfRangeWords()));

    }

    @Test
    void given_BritishSpelling_searchFindsToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("_isInDictAsAmericanSpelling", String.class, int.class, int.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(rangeAnalyser5000, "apologise", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "analyse", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "colour", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "centre", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "catalogue", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "defence", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "enrol", 0, 5000));
        assertTrue((boolean) method.invoke(rangeAnalyser5000, "programme", 0, 5000));
    }

    @Test
    void givenListOfSequences_searchInDict_findsOutOfRangeWords() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("findOutOfRangeWords", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> charSequences = new ArrayList<>();

        String shortFormsInPredSet_0 = "He's I'm THEY'RE notindictionary1 The boys' books' notinDict2"; // in predefined set
        String d_s_llSuffixes_1 = "Cat's notindictionary1 MOTHER's People'd Crowd'll notinDict2 children's people's mice's"; // in predefined set
        String traiLeadChars_2 = "([boy]) *cat plan: notindictionary1 end... red@ really?! \"drink\" notinDict2 #woman 'stop' 'stop'?! (('stop'))";
        String plurals_3 = "Pens CHILDREN notindictionary1 Feet girls notinDict2 NEWSPAPERS wolves";
        String negations_4 = "isn't AREN'T Won't hadn't notindictionary1 HASN'T notinDict2 weren't";
        String irregularVerbs_5 = "went GONE notindictionary1 Drank Taken notinDict2 BEEN was were";
        String s_Suffix_6 = "TAXES watches flies notindictionary1 Cries BOXES notinDict2 plays";
        String ed_Suffix_7 = "placed Liked notindictionary1 TRIED played notinDict2";
        String ing_suffix_8 = "notindictionary1 smiling  Working TRYING";
        String er_suffix_9 = "notindictionary1  larger BIGGER  Crazier";
        String er_suffix_10 = "notindictionary1  largest BIGGEST Craziest ";
        String varia_11 = " a  am AM PM aids AIDS Aids  AN  v Vs  _ -  *** notinDict2";
        String emptyString_12 = "";
        String blankString_13 = " ";
        String britishSpelling_14 = "apologise apologised apologising apologises analyse  centre catalogue defence notinDict2 enrol programme " +
                "colour colours coloures colourer colourest coloured colouring";
        String apostrophes_15 = "'People notinDict2 'like' wine'";
        String notFoundInDictWithNonLetterChars_16 = "quiz: splendor's 5G shan't";

        charSequences.add(shortFormsInPredSet_0);
        charSequences.add(d_s_llSuffixes_1);
        charSequences.add(traiLeadChars_2);
        charSequences.add(plurals_3);
        charSequences.add(negations_4);
        charSequences.add(irregularVerbs_5);
        charSequences.add(s_Suffix_6);
        charSequences.add(ed_Suffix_7);
        charSequences.add(ing_suffix_8);
        charSequences.add(er_suffix_9);
        charSequences.add(er_suffix_10);
        charSequences.add(varia_11);
        charSequences.add(emptyString_12);
        charSequences.add(blankString_13);
        charSequences.add(britishSpelling_14);
        charSequences.add(apostrophes_15);
        charSequences.add(notFoundInDictWithNonLetterChars_16);

        List<RangedText> rangedTexts = (List<RangedText>) method.invoke(rangeAnalyser5000, charSequences, 0, 5000);

        assertEquals(0, rangedTexts.get(0).getRangeStart());
        assertEquals(5000, rangedTexts.get(0).getRangeEnd());

        assertEquals(new ArrayList<>(List.of("notindictionary1", "notinDict2")).toString(), Arrays.toString(rangedTexts.get(0).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1", "notinDict2")).toString(), Arrays.toString(rangedTexts.get(1).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1", "notinDict2")).toString(), Arrays.toString(rangedTexts.get(2).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1", "notinDict2")).toString(), Arrays.toString(rangedTexts.get(3).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1", "notinDict2")).toString(), Arrays.toString(rangedTexts.get(4).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1", "notinDict2")).toString(), Arrays.toString(rangedTexts.get(5).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1", "notinDict2")).toString(), Arrays.toString(rangedTexts.get(6).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1", "notinDict2")).toString(), Arrays.toString(rangedTexts.get(7).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1")).toString(), Arrays.toString(rangedTexts.get(8).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1")).toString(), Arrays.toString(rangedTexts.get(9).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notindictionary1")).toString(), Arrays.toString(rangedTexts.get(10).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notinDict2")).toString(), Arrays.toString(rangedTexts.get(11).getOutOfRangeWords()));
        assertEquals("[]", Arrays.toString(rangedTexts.get(12).getOutOfRangeWords()));
        assertEquals("[]", Arrays.toString(rangedTexts.get(13).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notinDict2")).toString(), Arrays.toString(rangedTexts.get(14).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notinDict2")).toString(), Arrays.toString(rangedTexts.get(15).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("quiz", "splendor", "5G", "shan't")).toString(), Arrays.toString(rangedTexts.get(16).getOutOfRangeWords()));
    }

    //todo remove
    @Disabled
    @Test
    void stillFailing_searchInDict_findsOutOfRangeWords() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        Method method = rangeAnalyser5000.getClass().getDeclaredMethod("findOutOfRangeWords", List.class, int.class, int.class);
        method.setAccessible(true);

        List<String> charSequences = new ArrayList<>();
        String stillFailing_0 = "banned stopped colourest  coloured colour colouring colours notinDict2";


        charSequences.add(stillFailing_0);

        List<RangedText> rangedTexts = (List<RangedText>) method.invoke(rangeAnalyser5000, charSequences, 0, 5000);

        assertEquals(0, rangedTexts.get(0).getRangeStart());
        assertEquals(5000, rangedTexts.get(0).getRangeEnd());
        // assertEquals("[]", Arrays.toString(rangedTexts.get(0).getOutOfRangeWords()));
        assertEquals(new ArrayList<>(List.of("notinDict2")).toString(), Arrays.toString(rangedTexts.get(0).getOutOfRangeWords()));
    }

}
