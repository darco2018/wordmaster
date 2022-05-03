package com.ust.wordmaster.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserServiceTest_PureMockito {

    @InjectMocks
    private UserService userService;

    @Mock // instead of Spring's @MockBean
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private static final String USER_EMAIL = "sdf@ytr.com";
    private static final long USER_ID = 1L;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenMockedRepoFindsUserAndRealMapperMapsOK_getUserByID_returnsUserDTO() {

        User user = getUserWithEmailAndId();

        Mockito.when(this.userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(this.userMapper.mapToDTO(any(User.class), any(UserDTO.class))).thenCallRealMethod();

        //when
        UserDTO userDTO = this.userService.getOne(1L);

        Assertions.assertThat(userDTO).isNotNull();
        Assertions.assertThat(userDTO.getId()).isNotNull();
        Assertions.assertThat(userDTO.getId()).isEqualTo(USER_ID);
        Assertions.assertThat(userDTO.getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    public void givenMockedRepoFindsUserAndMockedMapperMapsOK_getUserByID_shouldReturnUserDTO() {

        User user = getUserWithEmailAndId();

        UserDTO userDTO = new UserDTO();
        userDTO.setId(USER_ID);
        userDTO.setEmail(USER_EMAIL);

        Mockito.when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(this.userMapper.mapToDTO(any(User.class), any(UserDTO.class))).thenReturn(userDTO);

        // when
        UserDTO found = this.userService.getOne(1L);

        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getId()).isNotNull();
        Assertions.assertThat(found.getId()).isEqualTo(USER_ID);
        Assertions.assertThat(found.getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    public void givenNoUserFound_getUser_shouldThrowResponseStatusException() {

        // Optional opt1 = Optional.ofNullable(null) =  Optional.empty();

        Mockito.when(this.userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null)); // = Optional.empty();
        //If the specified value is null, then this method returns an empty instance of the Optional class.

        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    this.userService.getOne(1L);
                }).withMessageMatching("404 NOT_FOUND");

    }

    @Test
    public void givenMockedMapperMapsOkAndMockedRepoFindsUser_createUser_returnsUserID() {

        User user = getUserWithEmailAndId();

        Mockito.when(this.userMapper.mapToEntity(any(UserDTO.class), any(User.class))).thenReturn(user);
        Mockito.when(this.userRepository.save(any(User.class))).thenReturn(user);

        //when
        Long foundUserID = this.userService.create(new UserDTO());

        Assertions.assertThat(foundUserID).isNotNull();
        Assertions.assertThat(foundUserID).isEqualTo(USER_ID);

    }

    @Test
    public void givenMockedRepoFindsUserAndMockedMapperMapsOk_updateUser_OK() {

        UserDTO userDTO = getUserDTOWithEmailAndId();
        User foundUser = getUserWithEmailAndId();

        Mockito.when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(foundUser));
        Mockito.when(this.userMapper.mapToEntity(userDTO, foundUser)).thenCallRealMethod();

        Mockito.doNothing().when(this.userRepository).deleteById(1L);

        //when
        this.userService.update(USER_ID, userDTO);

         /*update doesn't return anything, we can only
         - verify invocation on these methods
         - verify order of methods called
         - verify arguments captured
         - Verify no interaction with the whole mock occurred
         - Verify no interaction with a specific method
         - Verify there are no unexpected interactions after a given method invocation

         */

        verify(userRepository, times(1)).findById(USER_ID);

        // verify the same object that was retrieved from db is updated and saved
        verify(userRepository, times(1)).save(foundUser);


    }

    @Test
    public void testUpdateUserArgumentCapture() {

        UserDTO userDTO = getUserDTOWithEmailAndId();
        userDTO.setEmail("newEmail@gmail.com");
        User foundUser = getUserWithEmailAndId();


        ArgumentCaptor<User> userCapture = ArgumentCaptor.forClass(User.class);

        Mockito.when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(foundUser));
        Mockito.when(this.userMapper.mapToEntity(userDTO, foundUser)).thenCallRealMethod();
        Mockito.when(this.userRepository.save(userCapture.capture())).thenReturn(any(User.class));

        //when
        this.userService.update(USER_ID, userDTO);

        // we can verify the props from DTO are passed in the save() call
        assertEquals("newEmail@gmail.com", userCapture.getValue().getEmail());
        assertEquals(USER_ID, userCapture.getValue().getId());

    }

    private User getUserWithEmailAndId() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        return user;
    }

    private UserDTO getUserDTOWithEmailAndId() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(USER_ID);
        userDTO.setEmail(USER_EMAIL);
        return userDTO;
    }

}
