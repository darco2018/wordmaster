package com.ust.wordmaster.service.filtering;

import com.ust.wordmaster.dictionaryOLD.CSVParserOLD;
import com.ust.wordmaster.dictionaryOLD.CorpusDictionaryOLD;
import com.ust.wordmaster.dictionaryOLD.DictionaryEntryOLD;
import com.ust.wordmaster.service.filteringOLD.TextUnitCreator5000OLD;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class FilteringServiceImplTest {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";

    @BeforeAll
    public static void prepareDictionary() {

    }

    // testing a private method
    @Test
    void getWordsOutOfRangeStrings_considersShortenedFormsAsPresentInEachRange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<DictionaryEntryOLD> entriesFromFile = CSVParserOLD.parse(DICTIONARY_FILE);
        CorpusDictionaryOLD corpusDictionary2 = new CorpusDictionaryOLD("Corpus Dictionary from file", entriesFromFile);
        TextUnitCreator5000OLD filteringService = new TextUnitCreator5000OLD(corpusDictionary2);
    ////////////// PRIVATE METHOD ///////////////////
        Method method = TextUnitCreator5000OLD.class.getDeclaredMethod("getOutOfRangeWords", String[].class, int.class, int.class);
        method.setAccessible(true);

        String oddOneOut = "dinosaurs";
        String[] words = ("I'd eat " + oddOneOut + " He's stupid We're ok she's crazy").split(" ");
        //invoke() returns Object
        int[] notInRange = (int[]) method.invoke(filteringService, words, 1, 5000);
        assertArrayEquals(new int[]{2}, notInRange);

        words = ("these " + oddOneOut + " never").split(" ");
        System.out.println(Arrays.toString(words));
        notInRange = (int[]) method.invoke(filteringService, words, 1, 1000);
        System.out.println(Arrays.toString(notInRange));
        assertArrayEquals(new int[]{1}, notInRange);
    }



    // make you you don't test for words that appear more than once in the dictionary, eg in,feel, dance
    @Test
    void getWordsOutOfRangeStrings_worksOKwithDifferentRanges() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<DictionaryEntryOLD> entriesFromFile = CSVParserOLD.parse(DICTIONARY_FILE);
        CorpusDictionaryOLD corpusDictionary2 = new CorpusDictionaryOLD("Corpus Dictionary from file", entriesFromFile);
        TextUnitCreator5000OLD filteringService = new TextUnitCreator5000OLD(corpusDictionary2);

        Method method = TextUnitCreator5000OLD.class.getDeclaredMethod("getOutOfRangeWords", String[].class, int.class, int.class);
        method.setAccessible(true);

        String rank_1001 = "method";
        String rank_2002 = "mass";
        String[] words = ("He is " + rank_1001 + " in " + rank_2002).split(" ");
        //invoke() returns Object
        int[] notInRange = (int[]) method.invoke(filteringService, words, 1, 1000);
        assertArrayEquals(new int[]{2, 4}, notInRange);

        notInRange = (int[]) method.invoke(filteringService, words, 1, 2000);
        assertArrayEquals(new int[]{4}, notInRange);

        notInRange = (int[]) method.invoke(filteringService, words, 1000, 2000);
        assertArrayEquals(new int[]{0, 1, 3, 4}, notInRange);

        notInRange = (int[]) method.invoke(filteringService, words, 2000, 3000);
        assertArrayEquals(new int[]{0, 1, 2, 3}, notInRange);

        String[] words2 = ("in sensitivity").split(" "); //[, in, sensitivity]
        int[] notInRange2 = (int[]) method.invoke(filteringService, words2, 1, 1000);
        assertArrayEquals(new int[]{1}, notInRange);


        String rank_4500 = "sensitivity";
        words = ("They " + rank_1001 + " never " + rank_2002 + " " + rank_4500 + " notindictionary ").split(" ");
        notInRange2 = (int[]) method.invoke(filteringService, words, 3000, 4600);
        assertArrayEquals(new int[]{0, 1, 2, 3, 5}, notInRange2);

    }

    @Test
    void filter() {
    }
}