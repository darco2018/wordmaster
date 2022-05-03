package com.ust.wordmaster.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(value = "/load-userdata.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/delete-userdata.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //this will autowire TestRestTemplate
public class UserControllerTest_Integration {

    private static final String BASE_URL = "/api/users/";
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void createUser_OK() {

        UserDTO newUser = getUserDTO("stuff@stuff.com");
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(BASE_URL, newUser, Long.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

    }

    @Test
    public void findUser_ById_OK() {

        Long userID = 1999L;
        ResponseEntity<UserDTO> responseEntity = restTemplate.getForEntity(BASE_URL + userID, UserDTO.class);
        UserDTO userDTO = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(userID, userDTO.getId());
        assertEquals("testUser1@gmail.com", userDTO.getEmail());

    }

    @Test
    public void findAllUsers_OK() throws URISyntaxException {
        // thanks to exchange() we can use new ParameterizedTypeReference, which in turn allows to have type safety
        // in List<UserDTO> parametrizedUsers =  response.getBody(). getForEntity doesn't give this.
        ResponseEntity<List<UserDTO>> responseEntity = restTemplate.exchange(
                new RequestEntity<String>(HttpMethod.GET, new URI(BASE_URL)),
                new ParameterizedTypeReference<List<UserDTO>>() {
                });

        List<UserDTO> users = responseEntity.getBody();

        assertTrue(users.size() >= 1);
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

    }

    @Test
    public void updateUser_OK() {

        Long userID = 1999L;
        ResponseEntity<UserDTO> response1 = restTemplate.getForEntity(BASE_URL + userID, UserDTO.class);
        UserDTO userDTO = response1.getBody();
        assertEquals("testUser1@gmail.com", userDTO.getEmail());

        UserDTO updatedUser = UserDTO.builder().email("updated@gmail.com").id(userID).build();
        HttpEntity<UserDTO> requestEntity = new HttpEntity<UserDTO>(updatedUser);

        ResponseEntity<UserDTO> response = restTemplate.exchange(BASE_URL + userID, HttpMethod.PUT, requestEntity, UserDTO.class);
        // restTemplate.put(BASE_URL, UserDTO.class); -> returns void so we won't have ResponseEntity for assertions
        // & there's no putForEntity method

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<UserDTO> response3 = restTemplate.getForEntity(BASE_URL + userID, UserDTO.class);
        UserDTO retrieved = response3.getBody();
        assertEquals("updated@gmail.com", retrieved.getEmail());

    }

    @Test
    public void deleteUser_OK() {

        long initialUserCount = this.userRepository.count();

        Long userID = 1999L;
        ResponseEntity<UserDTO> responseEntity = restTemplate.getForEntity(BASE_URL + userID, UserDTO.class);
        UserDTO userDTO = responseEntity.getBody();
        assertEquals(userID, userDTO.getId());

        //restTemplate.delete("/api/users/" + 1000); // returns void, co we need to exchange()
        ResponseEntity<Void> response2 = restTemplate.exchange(BASE_URL + userID, HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class);

        assertNull(response2.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
        assertEquals(initialUserCount - 1, this.userRepository.count());

    }

    @Test
    public void deleteAllUsers_OK() {

        ResponseEntity<Void> response2 = restTemplate.exchange(BASE_URL , HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class);

        assertNull(response2.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
        assertEquals(0, this.userRepository.count());

    }


    private UserDTO getUserDTO(String... data) {
        return UserDTO.builder().email(data[0]).build();
    }

}
