package com.ust.wordmaster.exercise;

import com.ust.wordmaster.headline.HeadlineDTO;
import com.ust.wordmaster.headline.HeadlineFacade;
import com.ust.wordmaster.headline.HeadlineMapper;
import com.ust.wordmaster.headline.RangedTextDTO;
import com.ust.wordmaster.service.range.RangedText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ExerciseMakerTest {

    @Autowired
    private HeadlineFacade facade;

    private List<RangedText> rangedTexts;

    @BeforeEach
    void setUp(){
        rangedTexts= facade.processHeadlinesFromHtmlFile( 1, 5000, "bbc",
                Paths.get("bbc.html"));
    }

    @Test
    void givenRangedTexts_shouldRemoveThoseWithoutOutOfRangeWords(){

        ExerciseMaker exerciseMaker = new ExerciseMaker();
        List<RangedText> withWordsOutOfRangeOnly = exerciseMaker.removeIfNoOutOfRangeWords(rangedTexts);

        assertEquals(29, withWordsOutOfRangeOnly.size());
    }
}
