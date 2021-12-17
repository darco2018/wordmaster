package com.ust.wordmaster.newdictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVParserTest {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";

    @Test
    void returnsListOfDictionaryEntries() {

        List<DictionaryEntry> entries = CSVParser.parse(DICTIONARY_FILE);

        assertNotNull(entries);
        assertTrue(entries.size() > 0);
        assertEquals(entries.size(), 5000);
    }

}