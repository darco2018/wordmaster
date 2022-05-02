package com.ust.wordmaster.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    public User mapToEntity(final UserDTO userDTO, final User user) {
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        return user;
    }

}
