package ru.practicum.server.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.server.exception.NotFoundException;

import ru.practicum.server.user.dto.UserMapper;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;
import ru.practicum.server.user.service.UserServiceImpl;
import ru.practicum.server.user.storage.UserStorage;

import javax.transaction.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {
    private final UserDto userDto = makeUserDto("user", "user@user.com");
    private final User user = UserMapper.toUser(userDto);
    @Mock
    private UserService userService;
    @Mock
    private UserStorage userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @DisplayName("Добавляем новый user")
    void shouldAddUser() {
        when(userRepository.save(user)).thenReturn(user);
        UserDto userResult = userService.addUser(userDto);

        assertThat(userResult.getId(), notNullValue());
        assertThat(userResult.getName(), equalTo(userDto.getName()));
        assertThat(userResult.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @DisplayName("Обновляем user")
    void shouldUpdateUser() {
        UserDto userDtoUpdate = makeUserDto("userUpdate", "userUpd@user.com");
        User userUpdate = UserMapper.toUser(userDtoUpdate);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(userUpdate);

        UserDto userResult = userService.updateUser(1L, userDtoUpdate);

        assertThat(userResult.getId(), notNullValue());
        assertThat(userResult.getName(), equalTo(userDtoUpdate.getName()));
        assertThat(userResult.getEmail(), equalTo(userDtoUpdate.getEmail()));
    }

    @Test
    @DisplayName("Находим всех user")
    void shouldFindAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        Collection<UserDto> users = userService.getAllUsers();

        assertThat(users, notNullValue());
        assertEquals(1, users.size());
        assertThat(users, hasItem(userDto));
    }

    @Test
    @DisplayName("Находим user по id")
    void shouldFindUserById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDto userResult = userService.getUserById(1L);

        assertThat(userResult.getId(), notNullValue());
        assertEquals(userResult.getName(), "user");
        assertEquals(userResult.getEmail(), "user@user.com");
    }

    @Test
    @DisplayName("Находим user по id private-метод")
    void shouldFindUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User user = userService.getById(1L);

        assertThat(user.getId(), notNullValue());
        assertEquals(user.getName(), "user");
        assertEquals(user.getEmail(), "user@user.com");
    }

    @Test
    @DisplayName("Удаляем user по id не найд")
    void tryDeleteUserById() {
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(userRepository).deleteById(anyLong());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(1L)
        );

        assertEquals(exception.getMessage(), "Не найден пользователь c id=1.");
    }

    private UserDto makeUserDto(String name, String email) {
        return new UserDto(1L, name, email);
    }
}
