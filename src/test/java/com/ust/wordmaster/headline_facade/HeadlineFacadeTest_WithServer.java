package com.ust.wordmaster.headline_facade;

import com.ust.wordmaster.headline.HeadlineFacade;
import com.ust.wordmaster.service.range.RangedText;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class HeadlineFacadeTest_WithServer {

    @Autowired
    private HeadlineFacade facade;

    @Test
    void givenWebsiteName_shouldReturnParsedAndProcessedHeadlines() {

        List<RangedText> rangedTexts = facade.processHeadlinesFromServer( 0, 5000, "bbc");

        assertTrue(rangedTexts.size() > 50);
    }
}
