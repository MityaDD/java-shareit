package ru.practicum.server.user.service;

import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;

import java.util.Collection;

public interface UserService {
    UserDto getUserById(Long id);

    Collection<UserDto> getAllUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long id);

    User getById(Long id);
}
