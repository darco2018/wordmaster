package com.ust.wordmaster.newdictionary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DictionaryEntryTest {

    @Test
    void getWord_shouldReturnWord() {

        DictionaryEntry entry = new DictionaryEntry5000("hello");
        assertEquals("hello", entry.getWord());
    }

    @Test
    void givenBlankOrEmptyOrNullWord_getWord_shouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> new DictionaryEntry5000(" "));
        assertThrows(IllegalArgumentException.class, () -> new DictionaryEntry5000(""));
        assertThrows(NullPointerException.class, () -> new DictionaryEntry5000(null));
    }

    @Test
    void givenNullWordDate_setWordData_shouldThrowException() {
        DictionaryEntry entry = new DictionaryEntry5000("hello");
        assertThrows(NullPointerException.class, () -> entry.setWordData(null));
    }

    @Test
    void when_setWordData_getWordData_shouldReturnTheSameWordData() {
        DictionaryEntry entry = new DictionaryEntry5000("hello");
        WordData wordData = new WordData5000();
        entry.setWordData(wordData);
        assertSame(wordData, entry.getWordData());
    }

    @Test
    void compareTo_shouldCompareDictionaryEntriesLexicographically() {
        DictionaryEntry like = new DictionaryEntry5000("like") ;
        DictionaryEntry like2 = new DictionaryEntry5000("like") ;
        assertEquals(0, like.compareTo(like2));

        DictionaryEntry apple = new DictionaryEntry5000("apple") ;
        assertTrue( like.compareTo(apple) > 0);

        DictionaryEntry zoo = new DictionaryEntry5000("zoo") ;
        assertTrue( like.compareTo(zoo) < 0);
    }

    @Test
    void givenSameWord_equals_shouldReturnEqual() {
        DictionaryEntry like = new DictionaryEntry5000("like") ;
        DictionaryEntry like2 = new DictionaryEntry5000("like") ;
        assertTrue( like.equals(like2));
    }

    @Test
    void givenDifferentWords_equals_shouldReturnNotEqual() {
        DictionaryEntry like = new DictionaryEntry5000("like") ;
        DictionaryEntry capitalLike = new DictionaryEntry5000("Like") ;
        DictionaryEntry ike = new DictionaryEntry5000("ike") ;

        assertFalse( like.equals(capitalLike));
        assertFalse( like.equals(ike));
    }

    @Test
    void equalDictionaryEntries_shouldReturnSameHashCode() {
        DictionaryEntry like = new DictionaryEntry5000("like") ;
        DictionaryEntry like2 = new DictionaryEntry5000("like") ;
        assertTrue( like.equals(like2));
        assertTrue(like.hashCode() == like2.hashCode());
    }

}