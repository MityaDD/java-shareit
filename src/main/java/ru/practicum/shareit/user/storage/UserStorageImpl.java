package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage {
    private Long increment = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public Map<Long, User> getUsersMap() {
        return users;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(++increment);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public Boolean deleteUser(Long id) {
        users.remove(id);
        return !users.containsKey(id);
    }

}

