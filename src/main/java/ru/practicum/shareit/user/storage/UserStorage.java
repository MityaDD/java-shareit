package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStorage extends JpaRepository<User, Long>  {
}
