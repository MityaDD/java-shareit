package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage {
    private Long increment = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User getUserById(Long id) {
        validateId(id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        validateEmail(user);
        user.setId(++increment);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        validateId(user.getId());
        validateEmail(user);
        User updatedUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isEmpty()) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            updatedUser.setEmail(user.getEmail());
        }
        users.put(updatedUser.getId(), updatedUser);
        return users.get(updatedUser.getId());
    }

    @Override
    public Boolean deleteUser(Long id) {
        users.remove(id);
        return !users.containsKey(id);
    }

    private void validateId(Long id) {
        if (id > 0 && !users.containsKey(id)) {
            Log.andThrowNotFound("Не найден пользователь с id " + id);
        }
    }

    private void validateEmail(User user) {
        if (users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail())
                        && u.getId() != user.getId())) {
            Log.andThrowEmailConflict("Пользователь с таким email уже существует! " + user.getEmail());
        }
    }
}

