package com.ust.wordmaster.headline_controller;

import com.ust.wordmaster.headline.HeadlineDTO;
import com.ust.wordmaster.headline.HeadlineFacade;
import com.ust.wordmaster.headline.HeadlineMapper;
import com.ust.wordmaster.headline.RangedTextDTO;
import com.ust.wordmaster.service.range.RangedText;
import com.ust.wordmaster.service.range.RangedText5000;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HeadlineControllerTest_Mockito_WithServer {

    @MockBean
    private HeadlineFacade facade;

    @MockBean
    private HeadlineMapper mapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void givenFacadeAndMapperProduceWordsOutOfRange_whenGET_responseContainsTheSameOutOfRangeWords() throws Exception {

        // given
        String headline = "notInRange1 the notInRange2";
        String[] outsideRangeWords = new String[]{"notInRange1", "notInRange2"};

        RangedText rangedText = new RangedText5000(headline, 1, 5000);
        rangedText.setOutOfRangeWords(outsideRangeWords);
        given(facade.processHeadlinesFromServer(1, 5000, "bbc"))
                .willReturn(List.of(rangedText));

        HeadlineDTO headlineDTO = new HeadlineDTO();
        headlineDTO.setRangedTextDTOS(List.of(new RangedTextDTO(headline, outsideRangeWords)));
        given(mapper.toHeadlineDTO(1, 5000, "bbc", List.of(rangedText)))
                .willReturn(headlineDTO);

        // when
        ResponseEntity<HeadlineDTO> response =
                restTemplate.getForEntity("/headlines?website=bbc&rangeStart=1&rangeEnd=5000", HeadlineDTO.class);
        HeadlineDTO body = response.getBody();
        String[] responseOutOfRangeWords = body.getRangedTextDTOS().get(0).getOutOfRangeWords();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();
        assertEquals("notInRange1", Arrays.stream(responseOutOfRangeWords).findFirst().get());
        assertTrue(Arrays.stream(responseOutOfRangeWords).anyMatch(item -> item.equals("notInRange2")));
        assertTrue(Arrays.stream(responseOutOfRangeWords).noneMatch(item -> item.equals("the")));
    }

}
