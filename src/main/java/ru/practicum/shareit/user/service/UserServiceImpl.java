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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserStorage userStorage;

    @Override
    public UserDto getUserById(Long id) {
        log.info("Запрошен пользователь с id={}", id);
        validateId(id);
        return userMapper.toUserDto(userStorage.getUserById(id));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userStorage.getAllUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        validateEmail(user);
        userStorage.addUser(user);
        log.info("Добавлен новый пользователь: {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        userDto.setId(userId);
        User userFromNewDTO = userMapper.toUser(userDto);
        validateId(userId);
        validateEmail(userFromNewDTO);
        User userToBeUpdated = userStorage.getUserById(userId);
        checkAndSetFields(userFromNewDTO, userToBeUpdated);
        User updatedUser = userStorage.updateUser(userToBeUpdated);
        log.debug("Обновлен пользователь: {}", updatedUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
        log.debug("Удален пользователь с id={}", id);
    }


    private Map<Long, User> getUsersMap() {
        return userStorage.getUsersMap();
    }

    private void validateId(Long id) {
        if (id > 0 && !getUsersMap().containsKey(id)) {
            Log.andThrowNotFound("Не найден пользователь с id " + id);
        }
    }

    private void validateEmail(User user) {
        String mail = user.getEmail();
        if (isEmailAlreadyRegistered(mail, user.getId())) {
            Log.andThrowEmailConflict("Пользователь с таким email уже существует! " + mail);
        }
    }

    private boolean isEmailAlreadyRegistered(String mail, Long userId) {
        return getUsersMap().values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(mail)
                        && u.getId() != userId);
    }

    private void checkAndSetFields(User userFromNewDTO, User userToBeUpdated) {
        if (userFromNewDTO.getName() != null && !userFromNewDTO.getName().isEmpty()) {
            userToBeUpdated.setName(userFromNewDTO.getName());
        }
        if (userFromNewDTO.getEmail() != null && !userFromNewDTO.getEmail().isEmpty()) {
            userToBeUpdated.setEmail(userFromNewDTO.getEmail());
        }
    }
}