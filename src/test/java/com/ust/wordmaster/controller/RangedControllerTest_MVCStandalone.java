package com.ust.wordmaster.controller;

import com.ust.wordmaster.HeadlinesFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class) // prior to JUNit 5, we used runners (MockitoJUnitRunner)
class RangedControllerTest_MVCStandalone {

    private MockMvc mvc;

    @Mock // replaces Mockito.initMocks()
    private HeadlinesFacade facade;

    @InjectMocks
    private RangedController rangedController;

    @BeforeEach
    public void setUp(){
        // @Mock replaces Mockito.initMocks()

        // you can add filters and controller advice ,etc - in fact, you have to configure
        // any logic outside the controller because SPring context is off and nothing will be injected automatically
        mvc = MockMvcBuilders.standaloneSetup(rangedController).build();
    }

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

    @Test
    void givenRequestParams_returnsOK() throws Exception {

        RangedTextDTO rangedTextDTO = new RangedTextDTO("ccc happ xyz", new String[]{"ccc", "xyz"});
        RangedHeadlineDTO rangedHeadlineDTO = new RangedHeadlineDTO();
        rangedHeadlineDTO.setRangedTextList(List.of(rangedTextDTO));
        given(facade.processHeadlines("bbc", 1, 2000)).willReturn(rangedHeadlineDTO);

        //MockHttpServletResponse response =  the same assertions as below possible on the response object as well
        mvc.perform(get("/headlines?website=bbc&rangeStart=1&rangeEnd=2000").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("description")))
                .andExpect(content().json("{'dataSize':1}"))
                .andReturn().getResponse();

    }
}