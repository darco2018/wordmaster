package com.ust.wordmaster.headline_exercise;

import com.ust.wordmaster.headline.HeadlineFacade;
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
    void setUp() {
        rangedTexts = facade.processHeadlinesFromHtmlFile(1, 5000, "bbc",
                Paths.get("bbc.html"));
    }

    @Test
    void givenRangedTexts_shouldRemoveThoseWithoutOutOfRangeWords() {

        ExerciseMaker exerciseMaker = new ExerciseMaker();
        List<RangedText> withWordsOutOfRangeOnly = exerciseMaker.removeIfNoOutOfRangeWords(rangedTexts);

        withWordsOutOfRangeOnly.stream().forEach(rangedText -> System.out.println(rangedText));

        assertEquals(26, withWordsOutOfRangeOnly.size());
    }
}
