package ru.practicum.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.request.service.ItemRequestService;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDtoInput;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    private static final String HEADER = "X-Sharer-User-Id";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private User user;
    private User userTwo;
    private ItemRequestDtoInput item;
    private ItemRequestDto itemResponse;
    @MockBean
    private ItemRequestService itemRequestService;
    private static final Long USER_ID = 1L;

    @BeforeEach
    public void setUp() throws Exception {
        user = new User(1L, "user", "user@mail.ru");

        userTwo = new User(2L, "newUser", "newUser@yandex.ru");

        item = new ItemRequestDtoInput("Хотел бы взять дрель");

        itemResponse = ItemRequestDto.builder()
                .id(1L)
                .description(item.getDescription())
                .items(new ArrayList<>())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Кидаем exc, если Description пустое")
    public void tryAddItemRequestWithEmptyDescription() throws Exception {
        ItemRequestDtoInput item = new ItemRequestDtoInput(null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/requests")
                        .header(HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Кидаем exc, если нет user")
    public void tryGetItemRequestWithoutUser() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Получаем ItemRequest в виде пустого списка, если риквеста не было")
    public void shouldGetItemRequestWithoutRequest() throws Exception {
        when(itemRequestService.getAllRequestsByOwner(anyLong()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header(HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Получаем ItemRequest в виде пустого списка, если не заданы from, size")
    public void shouldGetItemRequestWithoutPaginationParams() throws Exception {
        when(itemRequestService.getAllRequestsByOwner(anyLong()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Кидаем exc, если нет from, size=0")
    public void tryGetItemRequestWithFrom0Size0() throws Exception {
        mockMvc.perform(get("/requests/all?from=0&size=0")
                        .header(HEADER, USER_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Кидаем exc, если нет from отрицательное")
    public void tryGetItemRequestWithFromMinSize20() throws Exception {
        mockMvc.perform(get("/requests/all?from=-1&size=20")
                        .header(HEADER, USER_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Кидаем exc, если нет size отрицательное")
    public void tryGetItemRequestWithFrom0SizeMin() throws Exception {
        mockMvc.perform(get("/requests/all?from=0&size=-1")
                        .header(HEADER, USER_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получаем ItemRequest для from=0 и size=20")
    public void shouldGetItemRequestWithFrom0Size20() throws Exception {
        when(itemRequestService.getAllRequestsByOtherUsers(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/requests/all?from=0&size=20")
                        .header(HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Добавляем ItemRequest")
    public void shouldAddItemRequest() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemResponse);

        when(itemRequestService.addRequest(any(), anyLong()))
                .thenReturn(itemResponse);

        mockMvc.perform(post("/requests")
                        .header(HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хотел бы взять дрель"))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @DisplayName("Получаем ItemRequest по id")
    public void shouldGetItemRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemResponse);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хотел бы взять дрель"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}