package ru.practicum.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private final UserDto userDto = new UserDto(1L,"user", "user@user.com");
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Добавляем новый user")
    void shouldCreateUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @DisplayName("Обновляем user")
    void shouldUpdateUser() throws Exception {
        createUser(userDto);
        UserDto userDtoUpdate = new UserDto(1L, "userUpdate", "user2@user.com");

        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDtoUpdate);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdate.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdate.getEmail())));
    }

    @Test
    @DisplayName("Получаем список всех пользователей")
    void shouldFindAllUsers() throws Exception {
        createUser(userDto);
        UserDto userDto2 = new UserDto(2L,"user2", "user2@user.com");
        createUser(userDto2);

        when(userService.getAllUsers())
                .thenReturn(Arrays.asList(userDto, userDto2));

        createRequest("", null, userDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
    }

    @Test
    @DisplayName("Получаем user по id")
    void shouldFindUserById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        createRequest("/{userId}", 1L, userDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @DisplayName("Удаляем user по id")
    void shouldDeleteUserById() throws Exception {
        createUser(userDto);
        doNothing().when(userService).deleteUser(1L);

        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }

    private void createUser(UserDto userDto) {
        when(userService.addUser(any()))
                .thenReturn(userDto);
    }

    private ResultActions createRequest(String url, Long param, UserDto dto) throws Exception {
        return mvc.perform(get("/users" + url, param)
                .content(mapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }
}
