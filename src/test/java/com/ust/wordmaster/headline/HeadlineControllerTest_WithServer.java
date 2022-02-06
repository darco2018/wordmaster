package com.ust.wordmaster.headline;

import com.ust.wordmaster.service.range.RangedText;
import com.ust.wordmaster.service.range.RangedText5000;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HeadlineControllerTest_WithServer {

    @MockBean
    private HeadlineFacade facade;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getRangedHeadlines() throws Exception {

        RangedText rangedText = new RangedText5000("ccc happ xyz", 1, 5000);
        rangedText.setOutOfRangeWords(new String[]{"ccc", "xyz"});

        HeadlineResponseDTO headlineResponseDTO = new HeadlineResponseDTO();
        headlineResponseDTO.setRangedTexts(List.of(rangedText));
        given(facade.processHeadlinesFromServer("bbc", 1, 5000)).willReturn(headlineResponseDTO);

        ResponseEntity<HeadlineResponseDTO> headlineResponse = restTemplate.getForEntity("/headlines?website=bbc&rangeStart=1&rangeEnd=5000", HeadlineResponseDTO.class);

        assertThat(headlineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(headlineResponse.getBody()).isNotNull();
        String[] outOfRange = headlineResponse.getBody().getRangedTextJSONList().get(0).getOutOfRangeWords();
        assertThat(Arrays.stream(outOfRange).findFirst().equals("ccc"));
        assertThat(Arrays.stream(outOfRange).anyMatch(item->item.equals("xyz")));
        assertThat(Arrays.stream(outOfRange).noneMatch(item->item.equals("bbc")));


    }

}
