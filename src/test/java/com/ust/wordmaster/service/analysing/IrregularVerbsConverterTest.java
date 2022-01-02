package com.ust.wordmaster.service.analysing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class IrregularVerbsConverterTest {

    private IrregularVerbsConverter converter;

    @BeforeEach
    public void setUp(){
        converter = new IrregularVerbsConverter();
    }

    @Test
    void givenPastVerbForm_returnsBaseForm() {

        assertEquals("go", converter.convertToBaseForm("went"));
        assertEquals("go", converter.convertToBaseForm("gone"));

        assertEquals("smell", converter.convertToBaseForm("smelled"));
        assertEquals("smell", converter.convertToBaseForm("smelt"));
        assertEquals("smell", converter.convertToBaseForm("smell"));

        assertEquals("tread", converter.convertToBaseForm("treaded"));
        assertEquals("tread", converter.convertToBaseForm("trod"));
        assertEquals("tread", converter.convertToBaseForm("trodden"));
        assertEquals("tread", converter.convertToBaseForm("tread"));

        assertEquals("can", converter.convertToBaseForm("could"));
    }

    public void whenWordIsNotIrregularVerbFrom_returnsWord() {

        assertEquals("play", converter.convertToBaseForm("play"));
        assertEquals("book", converter.convertToBaseForm("book"));
        assertEquals("nonsense", converter.convertToBaseForm("nonsense"));

    }

    @Test
    public void createConversionMap_generatesCorrectSizeOfEntries() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = IrregularVerbsConverter.class.getDeclaredMethod("createConversionMap");
        method.setAccessible(true);

        Map<String, String> irregularVerbsMap = (Map<String, String>) method.invoke(converter);

        assertEquals(262, irregularVerbsMap.size());
    }

    @Test
    public void readsFileInputCorrectlyAndSortsItAlphabetically() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = IrregularVerbsConverter.class.getDeclaredMethod("getIrregularVerbsSortedByBaseForm");
        method.setAccessible(true);

        List<IrregularVerbsConverter.IrregularVerb> irregularVerbs =
                (List<IrregularVerbsConverter.IrregularVerb>) method.invoke(converter);

        assertEquals(176, irregularVerbs.size());
        assertTrue(irregularVerbs.get(0).getBase().equalsIgnoreCase("abide"));
        assertTrue(irregularVerbs.get(irregularVerbs.size() - 2).getBase().equalsIgnoreCase("wring"));
        assertTrue(irregularVerbs.get(irregularVerbs.size() - 1).getBase().equalsIgnoreCase("write"));
    }
}