package com.ust.wordmaster.headline;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class HeadlineFacadeTest_FromFile {

    @Autowired
    private HeadlineFacade facade;

    @Test
    void givenBBCHtmlFile_whenParsedAndProcessed_shouldReturnNumberOfHeadlines(){

        HeadlineResponseDTO response = facade.processHeadlinesFromHtmlFile("bbc",0,5000,
                Paths.get("bbc.html"));
        List<RangedTextJSON> rangedTexts = response.getRangedTextJSONList();

        assertEquals(66, rangedTexts.size());
    }


}
