package com.ust.wordmaster.newdictionary;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CorpusDictionary5000Test {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";

    @Test
    void givenCorrectInput_getDictionary_returnsMapOfEntries() {
        List<DictionaryEntry> entries = CSVParser.parse(DICTIONARY_FILE);
        assertEquals(5000, entries.size());
        CorpusDictionary corpus = new CorpusDictionary5000("Dictionary", entries);

        Map<String, List<DictionaryEntry>> dictionary = corpus.getDictionary();

        assertNotEquals(0, dictionary.size());
        System.out.println(dictionary.get("in")); // null
        assertEquals(4352, dictionary.size());


        assertEquals(entries.size(),corpus.getNoOfEntries());

    }

    @Test
    void getDictionarySubset() {
    }

    @Test
    void containsEntry() {
    }

    @Test
    void getDictionaryEntry() {
    }
}