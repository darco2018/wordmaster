package com.ust.wordmaster.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAll() {
        return this.userRepository.findAll()
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return this.userRepository.save(user).getId();
    }

    public UserDTO getOne(final Long id) {
        return this.userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void update(final Long id, final UserDTO userDTO) {
        final User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(userDTO, user);
        this.userRepository.save(user);
    }

    public void delete(final Long id) {
        this.userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        return user;
    }

}
