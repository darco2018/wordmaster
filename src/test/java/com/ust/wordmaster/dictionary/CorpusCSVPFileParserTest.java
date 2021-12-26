package com.ust.wordmaster.dictionary;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CorpusCSVPFileParserTest {

    private static String csvFile = "dictionary5000.csv";
    private static List<DictionaryEntry> entries;

    @BeforeAll
    public static void setUp() {
        entries = CorpusCSVFileParser.parse(csvFile);
    }

    @Test
    void returnsListOfDictionaryEntries() {

        assertNotNull(entries);
        assertTrue(entries.size() > 0);
        Assertions.assertThat(entries.size()).isEqualTo(5000);
    }

    @Test
    public void given5000CSVLines_parsesEachIntoDictionaryEntryCorrectly() throws IOException {

        Assertions.assertThat(entries.get(0).getHeadword()).isEqualTo("the");
        Assertions.assertThat(entries.get(1).getHeadword()).isEqualTo("be");
        Assertions.assertThat(entries.get(2).getWordData().getPartOfSpeech()).isEqualTo("c");
        Assertions.assertThat(entries.get(4999).getWordData().getWord()).isEqualTo("till");
        Assertions.assertThat(   ((WordData5000) entries.get(4999).getWordData()).getFrequency()   ).isEqualTo(5079);
        Assertions.assertThat(   ((WordData5000) entries.get(4999).getWordData()).getDispersion()).isEqualTo(0.92);
    }

    @Test
    public void whenParsing_dictionaryEntryHasRequiredFields() {
        DictionaryEntry entry_1 =  entries.get(0);

        Assertions.assertThat(  ((WordData5000) entry_1.getWordData()).getRank()).isEqualTo(1);
        Assertions.assertThat(entry_1.getWordData().getWord()).isEqualTo("the");
        Assertions.assertThat(entry_1.getWordData().getPartOfSpeech()).isEqualTo("a");
        Assertions.assertThat(   ((WordData5000)entry_1.getWordData()).getFrequency()).isEqualTo(22038615);
        Assertions.assertThat(   ((WordData5000)entry_1.getWordData()).getDispersion()).isEqualTo(0.98);
    }



}