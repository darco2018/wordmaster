package com.ust.wordmaster.headline;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class HeadlineFacadeTest_WithServer {

    @Autowired
    private HeadlineFacade facade;

    @Test
    void testFacade() {
        HeadlineResponseDTO response = this.facade.processHeadlines("bbc", 0, 5000);

        assertTrue(response.getRangedTextJSONList().size() > 10);
        assertEquals(0, response.getRangeStart());
        assertEquals(5000, response.getRangeEnd());
        assertEquals("https://www.bbc.com/", response.getSource());
    }
}
