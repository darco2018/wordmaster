package com.ust.wordmaster.controller;

import com.ust.wordmaster.HeadlinesFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// context will load or rather the controller and its surrounding configuration: filters, controller advice
// no server involved, responses are fake
@WebMvcTest(RangedController.class)
public class RangedControllerTest_WebMVC {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private HeadlinesFacade facade;

    @Test
    void getRangedHeadlines() throws Exception {

        RangedTextDTO rangedTextDTO = new RangedTextDTO("ccc happ xyz", new String[]{"ccc", "xyz"});
        RangedHeadlineDTO rangedHeadlineDTO = new RangedHeadlineDTO();
        rangedHeadlineDTO.setRangedTextList(List.of(rangedTextDTO));
        given(facade.processHeadlines("bbc", 1, 5000)).willReturn(rangedHeadlineDTO);

        //MockHttpServletResponse response =  the same assertions as below possible on the response object as well
        mvc.perform(get("/headlines").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("description")))
                .andExpect(content().json("{'dataSize':1}"))
                .andReturn().getResponse();
    }
}
