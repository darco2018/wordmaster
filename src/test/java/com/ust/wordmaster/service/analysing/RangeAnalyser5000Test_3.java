package com.ust.wordmaster.service.analysing;

import com.ust.wordmaster.dictionary.CorpusCSVFileParser;
import com.ust.wordmaster.dictionary.CorpusDictionary;
import com.ust.wordmaster.dictionary.CorpusDictionary5000;
import com.ust.wordmaster.dictionary.DictionaryEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RangeAnalyser5000Test_3 {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    private static CorpusDictionary corpusDictionary;

    @BeforeAll
    static void setUp() {
        List<DictionaryEntry> entriesFromFile = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        corpusDictionary = new CorpusDictionary5000("Corpus Dictionary 5000 from file", entriesFromFile);
    }

    @Test
    void givenTokensPresentInDictionary_removesThem() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String[] shouldBePreserved = "am aids he's 20bl 55% go! went forbidden notIndictionary".split(" ");
        String[] shouldBeRemoved = "Mr go like he AM AIDS".split(" ");
        String[] tokens = Stream.of(shouldBePreserved, shouldBeRemoved)
                .flatMap(Stream::of)
                .toArray(String[]::new);

        List<String> actual = rangeAnalyser5000.removeIfFoundCasePreserved(Arrays.asList(tokens));

        assertEquals(shouldBePreserved.length, actual.size());
        assertEquals(Arrays.toString(shouldBePreserved), Arrays.toString(actual.toArray()));
    }

    @Test
    void givenHeadwordsInDifferentCase_removesThem() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String[] shouldBePreserved = "he's I'd 20bl 55% go! went forbidden notIndictionary".split(" ");
        String toUpperCase = "pm aids ok tv";
        String toLowerCase = " SMILE PLAY GOOD WRONG";
        String toTitleCase = " christmas african bible islam t-shirt";
        String[] shouldBeFoundAndRemoved = (toUpperCase + toLowerCase + toTitleCase).split(" ");

        String[] tokens = Stream.of(shouldBePreserved, shouldBeFoundAndRemoved)
                .flatMap(Stream::of)
                .toArray(String[]::new);

        List<String> actual = rangeAnalyser5000.removeIfFoundCaseModified(Arrays.asList(tokens));

        assertEquals(shouldBePreserved.length, actual.size());
        assertEquals(Arrays.toString(shouldBePreserved), Arrays.toString(actual.toArray()));
    }

    @Test
    void givenNullBlankEmptyString_removesThem() {
        RangeAnalyser5000 rangeAnalyser5000 = new RangeAnalyser5000(corpusDictionary);
        String[] shouldBePreserved = "he's I'd 20bl 55% go! went forbidden notIndictionary".split(" ");
        String[] shouldRemoved = new String[]{null, " ", ""};

        String[] tokens = Stream.of(shouldBePreserved, shouldRemoved)
                .flatMap(Stream::of)
                .toArray(String[]::new);

        List<String> actual = rangeAnalyser5000.removeIfNullBlankEmpty(Arrays.asList(tokens));

        assertEquals(shouldBePreserved.length, actual.size());
        assertEquals(Arrays.toString(shouldBePreserved), Arrays.toString(actual.toArray()));
    }


}