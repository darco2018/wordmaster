package com.ust.wordmaster.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

@SpringBootTest
public class UserServiceTest_Mockito {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @Test
    void givenMockedValidUserAndMockedMapper_whenGetOne_shouldFindUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("567@sth.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("567@sth.com");
        // pure service test as both dependencies mocked
        Mockito.when(this.userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(this.userMapper.mapToDTO(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UserDTO.class))).thenReturn(userDTO);

        // when
        UserDTO actual = this.userService.getOne(1L);

        Assertions.assertThat(actual.getId()).isEqualTo(1L);
        Assertions.assertThat(actual.getEmail()).isEqualTo("567@sth.com");

    }

    @Test
    void givenMockedValidUserAndRealMapper_whenGetOne_shouldFindUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("567@sth.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("567@sth.com");

        // mocked userRepository.findById but REAL method of userMappermapToDTO
        // NOTE: we simulatnously test Mapper and UserService - not good probably
        Mockito.when(this.userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(this.userMapper.mapToDTO(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UserDTO.class)))
                .thenCallRealMethod();

        // when
        UserDTO actual = this.userService.getOne(1L);

        Assertions.assertThat(actual.getId()).isEqualTo(1L);
        Assertions.assertThat(actual.getEmail()).isEqualTo("567@sth.com");

    }


}
