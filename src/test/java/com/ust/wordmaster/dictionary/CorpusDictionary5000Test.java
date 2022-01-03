package com.ust.wordmaster.dictionary;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CorpusDictionary5000Test {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";
    private static List<DictionaryEntry> ENTRIES = null;
    private static CorpusDictionary CORPUS = null;

    // By default, both JUnit 4 and 5 create a new instance of the test class before running each test method.
    @BeforeAll
    static void setUp() {
        ENTRIES = CorpusCSVFileParser.parse(DICTIONARY_FILE);
        assert (5000 == ENTRIES.size());

        CORPUS = new CorpusDictionary5000("Dictionary", ENTRIES);
    }

    @Test
    void getNoOfEntries_returnsCorrectNumber() {
        assertEquals(ENTRIES.size(), CORPUS.getNoOfEntries());
    }

    @Test
    void getDictionaryAsMap_returnsCorrectNoOfHeadwords() {
        Map<String, List<DictionaryEntry>> dictionaryAsMap = CORPUS.asMap();

        assertEquals(4352, dictionaryAsMap.size());
    }

    @Test
    void containsHeadword_returnsCorrectValues() {

        assertTrue(CORPUS.containsHeadword("sing"));
        assertTrue(CORPUS.containsHeadword("play"));
        assertFalse(CORPUS.containsHeadword("notindictionary"));
        assertFalse(CORPUS.containsHeadword(""));
        assertFalse(CORPUS.containsHeadword("   "));
    }

    @Test
    void givenCapitals_containsHeadword_returnsCorrectValues() {

        assertTrue(CORPUS.containsHeadword("AIDS"));
        assertTrue(CORPUS.containsHeadword("PM"));
        assertFalse(CORPUS.containsHeadword("TV"));
    }

    @Test
    void containsEntry_returnsCorrectValues() {
        assertTrue(CORPUS.containsEntry("dance", "n"));
        assertTrue(CORPUS.containsEntry("dance", "v"));
        assertFalse(CORPUS.containsEntry("dance", "xq"));
        assertFalse(CORPUS.containsEntry("notindictionary", "n"));
    }

    @Test
    void getEntry_returnsCorrectValues() {
        // 6,  in,i,6996437,0.98
        // 128,in,r,285035,0.98
        // 3038,in,c,9996,0.97
        DictionaryEntry entryForIn_i = CORPUS.getEntry("in", "i");
        assertEquals("in", entryForIn_i.getHeadword());
        assertEquals("in", entryForIn_i.getWordData().getWord());
        assertEquals("i", entryForIn_i.getWordData().getPartOfSpeech());
        assertEquals(6, ((WordData5000) entryForIn_i.getWordData()).getRank());

        DictionaryEntry entryForIn_r = CORPUS.getEntry("in", "r");
        assertEquals("in", entryForIn_r.getHeadword());
        assertEquals("in", entryForIn_r.getWordData().getWord());
        assertEquals("r", entryForIn_r.getWordData().getPartOfSpeech());
        assertEquals(128, ((WordData5000) entryForIn_r.getWordData()).getRank());

        DictionaryEntry entryForNonexistent = CORPUS.getEntry("notindictionary", "xyz");
        assertNull(entryForNonexistent);
    }

    @Test
    void getEntriesByHeadword_returnsCorrectValues() {
        List<DictionaryEntry> entriesForIN = CORPUS.getEntriesByHeadword("in");

        assertAll(
                "different headwords should return different number of entries",
                () -> assertEquals(3, entriesForIN.size()),
                () -> assertEquals(0, CORPUS.getEntriesByHeadword("dsfhjfdsa").size()),
                () -> assertEquals(1, CORPUS.getEntriesByHeadword("galaxy").size())
        );

        List<String> partsOfSpeech = new ArrayList<>();
        entriesForIN.forEach(e -> partsOfSpeech.add(e.getWordData().getPartOfSpeech()));

        assertAll(
                "entries with different parts of speech are returned",
                () -> assertTrue(partsOfSpeech.contains("i")),
                () -> assertTrue(partsOfSpeech.contains("r")),
                () -> assertTrue(partsOfSpeech.contains("c")),
                () -> assertFalse(partsOfSpeech.contains("xyz"))
        );
    }

    @Test
    void isHeadwordInRankRange() {
        CorpusDictionary5000 c = ((CorpusDictionary5000)CORPUS);

        assertAll(
                "shows correct range",
               () -> assertTrue(c.isHeadwordInRankRange("in",  1,  100)), // 6, 128, 3038
               () -> assertTrue(c.isHeadwordInRankRange("in",  0,  5000)),
                () -> assertFalse(c.isHeadwordInRankRange("in",  4500,  5000)),

                () -> assertTrue(c.isHeadwordInRankRange("galaxy",  3000,  3111)), //3041
                () -> assertFalse(c.isHeadwordInRankRange("galaxy",  3112,  5000)),
               () -> assertTrue(c.isHeadwordInRankRange("galaxy",  1,  3499))
        );

    }

    @Test
    public void createsCorrectNumberOfWordsWithSingleDefinitionsAndMultipleDefinitions() throws IOException {

        // assert
        Map<String, List<DictionaryEntry>> dictionaryAsMap = CORPUS.asMap();


        int headwordsWithMultipleDefinitions = 0;
        int headwordsWithSingleDefinition = 0;
        for (String headword : dictionaryAsMap.keySet()) {
            if (dictionaryAsMap.get(headword).size() > 1) {
                headwordsWithMultipleDefinitions++;
            } else {
                headwordsWithSingleDefinition++;
            }
        }
        // assert
        int expectedNoOfHeadwords = 4352;
        int expectedNoOfHeadwordsWIthMultipleDefs = 604;
        int expectedNoOfHEadwordsWithSingleDef = expectedNoOfHeadwords - expectedNoOfHeadwordsWIthMultipleDefs;
        Assertions.assertThat(dictionaryAsMap.size()).isEqualTo(expectedNoOfHeadwords);
        Assertions.assertThat(headwordsWithMultipleDefinitions).isEqualTo(expectedNoOfHeadwordsWIthMultipleDefs);
        Assertions.assertThat(headwordsWithSingleDefinition).isEqualTo(expectedNoOfHEadwordsWithSingleDef);
    }

    @Test
    public void givenMultipleDefinitionsOfWord_whenCreatingDictionary_allDefinitionsArePreserved() {

        Map<String, List<DictionaryEntry>> dictionaryAsMap = CORPUS.asMap();

        // assert
        List<DictionaryEntry> danceDefintions = dictionaryAsMap.get("dance");
        Assertions.assertThat(danceDefintions.size()).isEqualTo(2);
        Assertions.assertThat(danceDefintions)
                .extracting("headword")
                .containsOnly("dance");
        Assertions.assertThat(danceDefintions)
                .extracting("wordData")
                .extracting("partOfSpeech")
                .containsOnly("n", "v");
    }


}