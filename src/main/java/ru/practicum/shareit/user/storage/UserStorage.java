package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User getUserById(Long id);

    Collection<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    Boolean deleteUser(Long id);
}
