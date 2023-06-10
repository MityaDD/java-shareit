package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserStorageTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired

    private UserStorage userStorage;

    @Test
    @DisplayName("Проверяем save и find")
    void testUserDto() throws Exception {
        LocalDateTime time = LocalDateTime.now();

        User user = new User(2L, "Vladimir", "vova-boss@yandex.ru");
        User savedUser = userStorage.save(user);

        Long savedUserId = savedUser.getId();

        User retrievedUser = entityManager.find(User.class, savedUserId);

        assertThat(retrievedUser).isEqualTo(savedUser);
    }

    @Test
    @DisplayName("Дубликат email")
    void tryUseDuplicateEmail() throws Exception {
        LocalDateTime time = LocalDateTime.now();

        User user = new User(2L, "Vladimir", "vova-boss@yandex.ru");
        User savedUser = userStorage.save(user);

        Long savedUserId = savedUser.getId();

        User retrievedUser = entityManager.find(User.class, savedUserId);

        assertThat(retrievedUser).isEqualTo(savedUser);

        assertThrows(DataIntegrityViolationException.class, () -> {
            User userDuplicate = new User(3L, "Vladimir", "vova-boss@yandex.ru");
            User savedUserDuplicate = userStorage.save(user);
        });
    }
}

