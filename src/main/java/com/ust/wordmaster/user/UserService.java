package com.ust.wordmaster.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDTO> getAll() {
        return this.userRepository.findAll()
                .stream()
                .map(user -> this.userMapper.mapToDTO(user, new UserDTO()))
                .toList();

        /* Test:
        - Are really ALL users returned?
        */
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        this.userMapper.mapToEntity(userDTO, user);
        return this.userRepository.save(user).getId();

        /* Test:
        - Is really this new User in db after the operation?
        - what if UserDTO is null? >> Controller should check for valid UserDTO
        */
    }

    public UserDTO getOne(final Long id) {
        return this.userRepository.findById(id)
                .map(user -> this.userMapper.mapToDTO(user, new UserDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        /* Optional<User> userOpt = this.userRepository.findById(id);
        User user = userOpt.get();
        UserDTO userDTO = this.userMapper.mapToDTO(user, new UserDTO());
        return userDTO;*/

        /* Test:
        - Is really this new User in db after the operation?
        - what if id is null? >> Controller should check for non-null param
        - Is ResponseStatusException thrown when User is not found?
        */
    }

    public void update(final Long id, final UserDTO userDTO) {
        final User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.userMapper.mapToEntity(userDTO, user);
        this.userRepository.save(user);

        /* Test:
        - Does User returned by save(user) have updated values?
        - Does User in save(user) have the same props as UserDTO? - actually tests Mapper
        - Is ResponseStatusException when User is not found?
        */
    }

    public void delete(final Long id) {

        this.userRepository.deleteById(id);

        /* Test:
        - what happens when User with this id not found?
        - what happens when id is null?! >> Controller should check for null!
        - Is there really no such User after the operation?!
        */
    }


    public void deleteAllUsers() {
        this.userRepository.deleteAll();
    }
}
