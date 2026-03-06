package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto getUser(Long userId);

    void deleteUser(Long userId);
}