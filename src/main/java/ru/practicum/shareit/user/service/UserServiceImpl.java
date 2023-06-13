package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.Log;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User getById(Long id) {
        Optional<User> user = userStorage.findById(id);
        if (!user.isPresent()) {
            Log.andThrowNotFound(String.format("Не найден пользователь c id=%d.", id));
        }
        return user.get();
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Запрошен пользователь с id={}", id);
        return UserMapper.toUserDto(getById(id));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userStorage.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User newUser = userStorage.save(user);
        log.info("Добавлен новый пользователь: {}", user);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User userToBeUpdated = getById(userId);
        checkAndSetFields(userDto, userToBeUpdated);
        User updatedUser = userStorage.save(userToBeUpdated);
        log.info("Обновлён пользователь: {}", userToBeUpdated);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        validateId(id);
        userStorage.deleteById(id);
        log.debug("Удален пользователь с id={}", id);
    }

    private void validateId(Long userId) {
        if (!userStorage.existsById(userId)) {
            Log.andThrowNotFound("Не найден пользователь c id=" + userId);
        }
    }

    private void checkAndSetFields(UserDto userDto, User userToBeUpdated) {
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            userToBeUpdated.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            userToBeUpdated.setEmail(userDto.getEmail());
        }
    }
}