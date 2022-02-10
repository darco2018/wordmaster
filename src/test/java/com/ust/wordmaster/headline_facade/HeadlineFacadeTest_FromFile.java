package com.ust.wordmaster.headline_facade;

import com.ust.wordmaster.exercise.ExerciseMaker;
import com.ust.wordmaster.headline.HeadlineDTO;
import com.ust.wordmaster.headline.HeadlineFacade;
import com.ust.wordmaster.headline.HeadlineMapper;
import com.ust.wordmaster.headline.RangedTextDTO;
import com.ust.wordmaster.service.range.RangedText;
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
    void givenBBCHtmlFile_whenParsedAndProcessed_shouldReturnExactNumberOfHeadlines() {

        List<RangedText> rangedTexts = facade.processHeadlinesFromHtmlFile( 0, 5000, "bbc",
                Paths.get("bbc.html"));

        assertEquals(66, rangedTexts.size());
    }

    }
