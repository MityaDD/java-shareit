package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto getUserById(Long id);

    Collection<UserDto> getAllUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long id);

    Boolean deleteUser(Long id);
}
