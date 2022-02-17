package com.ust.wordmaster.headline_exercise;

import com.ust.wordmaster.headline.HeadlineFacade;
import com.ust.wordmaster.service.range.RangedText;
import com.ust.wordmaster.service.range.RangedText5000;
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
        List<RangedText> withWordsOutOfRangeOnly = exerciseMaker.getTextsWithNonZeroNumberOutOfRangeWords(this.rangedTexts);

        assertEquals(26, withWordsOutOfRangeOnly.size());
    }

    @Test
    void givenRangedTexts_whenCreateExercise_shouldCreateCorrectContentAndNoOfQuizItems() {

        RangedText rangedText0 = new RangedText5000("The girl is nice.", 1, 5000);
        rangedText0.setOutOfRangeWords(new String[]{});
        RangedText rangedText1 = new RangedText5000("The lion lives in the jungle.", 1, 5000);
        rangedText1.setOutOfRangeWords(new String[]{"lion", "jungle"});
        RangedText rangedText2 = new RangedText5000("Do people eat apples?", 1, 5000);
        rangedText2.setOutOfRangeWords(new String[]{"eat"});
        RangedText rangedText3 = new RangedText5000("Never play with spiders or insects!!!", 1, 5000);
        rangedText3.setOutOfRangeWords(new String[]{"spiders", "insects"});
        RangedText rangedText4 = new RangedText5000("They acted with new dynamics!!!", 1, 5000);
        rangedText4.setOutOfRangeWords(new String[]{"dynamics"});
        List<RangedText> rangedTexts = List.of(rangedText0, rangedText1, rangedText2, rangedText3, rangedText4);

        //when
        ExerciseMaker exerciseMaker = new ExerciseMaker();
        HeadlineExercise exercise1 = exerciseMaker.createExercise(rangedTexts, "<?>", " #*", 4);
        HeadlineExercise exercise2 = exerciseMaker.createExercise(rangedTexts, "<?>", " #*", 5);
        HeadlineExercise exercise3 = exerciseMaker.createExercise(rangedTexts, "<?>", " #*", 6);

        // then
        assertEquals("""
                The <?> lives in the <?>. #*lion jungle
                Do people <?> apples? #*eat""", exercise1.getContent());

        assertEquals("""
                The <?> lives in the <?>. #*lion jungle
                Do people <?> apples? #*eat
                Never play with <?> or <?>!!! #*spiders insects""", exercise2.getContent());

        assertEquals("""
                The <?> lives in the <?>. #*lion jungle
                Do people <?> apples? #*eat
                Never play with <?> or <?>!!! #*spiders insects
                They acted with new <?>!!! #*dynamics""", exercise3.getContent());
    }

    @Test
    void givenRangedTextsFromFile_whenCreateExercise_shouldCreateCorrectContentAndNoOfQuizItems() {

        //when
        ExerciseMaker exerciseMaker = new ExerciseMaker();
        HeadlineExercise exercise1 = exerciseMaker.createExercise(this.rangedTexts, "_____", " |||", 4);
        HeadlineExercise exercise2 = exerciseMaker.createExercise(this.rangedTexts, ".....", " ans: ", 5);
        HeadlineExercise exercise3 = exerciseMaker.createExercise(this.rangedTexts, "<?>", " #*", 8);
        HeadlineExercise exercise4 = exerciseMaker.createExercise(this.rangedTexts, "<?>", " #*", 9);

        // then
        assertEquals("""
                Coronavirus _____ hits cruise ship off Japan |||outbreak
                Kenya's women are _____ - but what is roll ball? |||champs
                Cement _____ become celebrities in China _____ |||mixers lockdown""", exercise1.getContent());

        assertEquals("""
                Coronavirus ..... hits cruise ship off Japan ans: outbreak
                Kenya's women are ..... - but what is roll ball? ans: champs
                Cement ..... become celebrities in China ..... ans: mixers lockdown
                Sun ..... completes preparation for launch ans: probe""", exercise2.getContent());

        assertEquals("""
                Coronavirus <?> hits cruise ship off Japan #*outbreak
                Kenya's women are <?> - but what is roll ball? #*champs
                Cement <?> become celebrities in China <?> #*mixers lockdown
                Sun <?> completes preparation for launch #*probe
                Coronavirus: Airline asks staff to take <?> leave #*unpaid
                HIV vaccine hopes <?> by trial results #*dashed""", exercise3.getContent());

        assertEquals("""
                Coronavirus <?> hits cruise ship off Japan #*outbreak
                Kenya's women are <?> - but what is roll ball? #*champs
                Cement <?> become celebrities in China <?> #*mixers lockdown
                Sun <?> completes preparation for launch #*probe
                Coronavirus: Airline asks staff to take <?> leave #*unpaid
                HIV vaccine hopes <?> by trial results #*dashed
                Mobile operators <?> on '<?>' costs #*clash notspots""", exercise4.getContent());

    }
}
