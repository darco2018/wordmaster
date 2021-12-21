package com.ust.wordmaster.dictionary;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CorpusCSVPFileParserTest {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";

    @Test
    void returnsListOfDictionaryEntries() {

        List<DictionaryEntry> entries = CorpusCSVFileParser.parse(DICTIONARY_FILE);

        assertNotNull(entries);
        assertTrue(entries.size() > 0);
        assertEquals(entries.size(), 5000);
    }

}