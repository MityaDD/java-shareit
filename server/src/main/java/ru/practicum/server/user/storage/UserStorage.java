package ru.practicum.server.user.storage;

import ru.practicum.server.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStorage extends JpaRepository<User, Long>  {
}
