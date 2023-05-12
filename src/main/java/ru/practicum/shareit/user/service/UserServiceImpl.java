package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserStorage userStorage;

    @Override
    public UserDto getUserById(Long id) {
        return userMapper.toUserDto(userStorage.getUserById(id));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userStorage.addUser(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        userDto.setId(id);
        return userMapper.toUserDto(userStorage.updateUser(userMapper.toUser(userDto)));
    }

    @Override
    public Boolean deleteUser(Long id) {
        return userStorage.deleteUser(id);
    }
}

