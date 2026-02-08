package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto userDto) {
        throwIfEmailExists(userDto.getEmail());

        User user = userMapper.toUser(userDto);
        User createdUser = userRepository.createUser(user);
        return userMapper.toUserDto(createdUser);
    }

    public UserDto getUser(long id) {
        User user = getUserOrElseThrow(id);
        return userMapper.toUserDto(user);
    }

    public UserDto updateUser(long id, UserDto userDto) {
        User user = getUserOrElseThrow(id);

        updateUserFields(user, userDto);
        userRepository.updateUser(user);
        return userMapper.toUserDto(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    private void updateUserFields(User user, UserDto userUpdateDto) {
        String name = userUpdateDto.getName();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        String email = userUpdateDto.getEmail();
        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            throwIfEmailExists(email);
            user.setEmail(email);
        }
    }

    private User getUserOrElseThrow(long id) {
        return userRepository.getUser(id)
            .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private void throwIfEmailExists(String email) {
        if (userRepository.getUserByEmail(email).isPresent()) {
            throw new DuplicateEmailException("User with email " + email + " already exists");
        }
    }
}