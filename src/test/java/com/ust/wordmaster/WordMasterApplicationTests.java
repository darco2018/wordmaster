package com.ust.wordmaster;


import com.ust.wordmaster.headline.HeadlineDTO;
import com.ust.wordmaster.headline.RangedTextDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordMasterApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void getHeadlines() {
		ResponseEntity<HeadlineDTO> response = restTemplate.getForEntity("/headlines?website=bbc&rangeStart=1&rangeEnd=5000", HeadlineDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		HeadlineDTO body = response.getBody();
		assert body != null;
		List<RangedTextDTO> rangedTexts = body.getRangedTextDTOS();

		assertThat(body).isNotNull();
		assertEquals(1, body.getRangeStart());
		assertEquals(5000, body.getRangeEnd());
		assertTrue(rangedTexts.size() > 30);
		assertTrue(body.getSource().equalsIgnoreCase("bbc"));

	}

}
