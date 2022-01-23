package com.ust.wordmaster.controller;

import com.ust.wordmaster.HeadlinesFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RangedControllerTest_WithServer {

    @MockBean
    private  HeadlinesFacade facade;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getRangedHeadlines() throws Exception {

        RangedTextDTO rangedTextDTO = new RangedTextDTO("ccc happ xyz", new String[]{"ccc", "xyz"});
        RangedHeadlineDTO rangedHeadlineDTO = new RangedHeadlineDTO();
        rangedHeadlineDTO.setRangedTextList(List.of(rangedTextDTO));
        given(facade.processHeadlines("bbc", 1, 5000)).willReturn(rangedHeadlineDTO);

        ResponseEntity<RangedHeadlineDTO> headlineResponse = restTemplate.getForEntity("/headlines?website=bbc&rangeStart=1&rangeEnd=5000", RangedHeadlineDTO.class);

        assertThat(headlineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(headlineResponse.getBody()).isNotNull();
        String[] outOfRange = headlineResponse.getBody().getRangedTextDTOList().get(0).getOutOfRangeWords();
        assertThat(Arrays.stream(outOfRange).findFirst().equals("ccc"));
        assertThat(Arrays.stream(outOfRange).anyMatch(item->item.equals("xyz")));
        assertThat(Arrays.stream(outOfRange).noneMatch(item->item.equals("bbc")));


    }

}
