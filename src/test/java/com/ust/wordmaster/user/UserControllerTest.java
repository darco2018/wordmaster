package com.ust.wordmaster.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    ////////////////////// HAPPY PATHS ///////////////////////////////////////////////

    @Test
    public void findUser_byID_OK() throws Exception {

        UserDTO userDTO = UserDTO.builder().email("asd@pc.com").id(1L).build();

        Mockito.when(this.userService.getOne(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                //.andExpect(MockMvcResultMatchers.header().string("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", org.hamcrest.Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("asd@pc.com"))
                .andExpect(MockMvcResultMatchers.content().string(new ObjectMapper().writeValueAsString(userDTO)));

        Mockito.verify(this.userService, times(1)).getOne(1L);

    }

    @Test
    public void findAllUsers_OK() throws Exception {

        List<UserDTO> users = List.of(
                UserDTO.builder().email("user1@pc.com").id(1L).build(),
                UserDTO.builder().email("user2@pc.com").id(2L).build());

        Mockito.when(this.userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/api/users/").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))

                //.andExpect(MockMvcResultMatchers.jsonPath("$.users").exists()); FAILS
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()) // "$" root in JSONPath Syntax : https://support.smartbear.com/alertsite/docs/monitors/api/endpoint/jsonpath.html
                .andExpect(MockMvcResultMatchers.jsonPath("$", org.hamcrest.Matchers.hasSize(2)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("user1@pc.com"))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", org.hamcrest.Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("user2@pc.com"))

                .andExpect(MockMvcResultMatchers.content().string(new ObjectMapper().writeValueAsString(users)));

        Mockito.verify(this.userService, times(1)).getAll();

    }

    @Test
    public void createUser_OK() throws Exception {

        UserDTO userDTO = UserDTO.builder().email("asd@pc.com").build();

        Mockito.when(this.userService.create(userDTO)).thenReturn(1L);

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON) //for POST() !!!
                        .content(new ObjectMapper().writeValueAsString(userDTO))
                        .accept(MediaType.APPLICATION_JSON))


                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("1"));

        Mockito.verify(this.userService, times(1)).create(userDTO); // relies on equals in User

    }

    @Test
    public void updateUser_OK() throws Exception {

        UserDTO userDTO = UserDTO.builder().email("asd@pc.com").id(1L).build();

        Mockito.doNothing().when(this.userService).update(1L, userDTO);

        mockMvc.perform(put("/api/users/{1L}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO))
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isOk());
        // .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));  FAILS
        // for ResponseEntity<Void> -> the body is empty
        // as the content-type only applies to the body, and is therefore redundant.

        Mockito.verify(this.userService, times(1)).update(1L, userDTO);

    }

    @Test
    public void deleteUser_OK() throws Exception {

        Mockito.doNothing().when(this.userService).delete(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(this.userService, times(1)).delete(1L);

    }

    ////////////////////// SAD PATHS ///////////////////////////////////////////////


    @Test
    public void findUser_byInvalidID_404() throws Exception {

        Mockito.when(this.userService.getOne(1000L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/users/{id}", 1000L)
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isNotFound());

        Mockito.verify(this.userService, times(1)).getOne(1000L);

    }

    @Test
    public void updateUser_byInvalidId_404() throws Exception {

        UserDTO userDTO = UserDTO.builder().email("asd@pc.com").id(1000L).build();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(this.userService).update(1000L, userDTO);

        mockMvc.perform(put("/api/users/{1L}", 1000L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO))
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isNotFound());

        Mockito.verify(this.userService, times(1)).update(1000L, userDTO);

    }

    @Test
    public void deleteUser_ByInvalidID_404() throws Exception {

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(this.userService).delete(1000L);

        mockMvc.perform(delete("/api/users/{id}", 1000L)
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isNotFound());

        Mockito.verify(this.userService, times(1)).delete(1000L);

    }

    @Test
    public void createUser_withInvalidUserJSON_400BadRequest() throws Exception {

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON) //for POST() !!!
                        .content(new ObjectMapper().writeValueAsString("Invalid User data"))
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isBadRequest());

        Mockito.verify(this.userService, times(0)).create(any());

    }

    @Test
    public void updateUser_withInvalidUserJSON_400BadRequest() throws Exception {

        mockMvc.perform(put("/api/users/{1L}", 1000L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString("Invalid user data"))
                        .accept(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isBadRequest());

        Mockito.verify(this.userService, times(0)).update(any(), any());

    }
}
