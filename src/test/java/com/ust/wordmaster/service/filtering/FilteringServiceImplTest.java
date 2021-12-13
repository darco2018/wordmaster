package com.ust.wordmaster.service.filtering;

import com.ust.wordmaster.dict2.CSVParser2;
import com.ust.wordmaster.dict2.CorpusDictionary2;
import com.ust.wordmaster.dict2.DictionaryEntry2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilteringServiceImplTest {

    public static final String DICTIONARY_FILE = "dictionary5000.csv";

    @BeforeAll
    public static void prepareDictionary(){

    }

    // testing a private method
    @Test
    void getWordsOutOfRangeStrings() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<DictionaryEntry2> entriesFromFile = CSVParser2.parse(DICTIONARY_FILE);
        CorpusDictionary2 corpusDictionary2 = new CorpusDictionary2("Corpus Dictionary from file", entriesFromFile);
        FilteringServiceImpl filteringService = new FilteringServiceImpl(corpusDictionary2);

        Method method = FilteringServiceImpl.class.getDeclaredMethod("getOutOfRangeWords", String[].class, int.class, int.class);
        method.setAccessible(true);

        String str = "I'd like bananas and dinosaurs";
        String[] words = str.split(" ");

        //invoke() returns Object
        int[] indexes = (int[])method.invoke(filteringService, words, 0, 5000);

        assertArrayEquals(new int[] {0,4}, indexes);
    }


    @Test
    void filter() {
    }
}