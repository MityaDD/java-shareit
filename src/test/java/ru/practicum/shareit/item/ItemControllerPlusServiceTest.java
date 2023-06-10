package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerPlusServiceTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final ItemService service;
    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        user = new User(1L, "user", "user@user.com");
        String jsonUser = objectMapper.writeValueAsString(user);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));

        Item item = new Item(1L, "Дрель", "Простая дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        Long userId = 1L;
        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem));
    }

    @Test
    @DisplayName("Добавляем item и возвращаем 200.ОК")
    void addItemWhen200IsReturned() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Простая дрель", user, true, null);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    @DisplayName("Добавляем item и возвращаем 400.BadRequest")
    void addItemWhen400IsReturned() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "Дрель", "", user, true, null);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("Добавляем item и возвращаем 404.NotFound")
    void addItemWhen404IsReturned() throws Exception {
        Long userId = 2L;
        ItemDto itemDto = new ItemDto(1L, "Дрель00000", "Простая дрель0000", user, true, null);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Обновляем item и возвращаем 200.ОК")
    void updateItemWhen200IsReturned() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Простая дрель", user, true, null);
        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    @DisplayName("Обновляем item и возвращаем 404.NotFound")
    void updateItemWhen404IsReturned() throws Exception {
        Long userId = 2L;
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Простая дрель", user, true, null);
        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }


    @Test
    @DisplayName("Возвращаем item по id")
    public void GetItemByIdTest() throws Exception {
        Integer itemId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    @DisplayName("Ловим BadRequest, если создаем item без X-Sharer-User-Id")
    public void shouldItemWithoutXSharerUserId() throws Exception {
        Item item = new Item(2L, "Дрель", "Простая дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Ловим NotFound, если user не существует")
    public void shouldItemWithNotFoundUser() throws Exception {
        Item item = new Item(2L, "Дрель", "Простая дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 999L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Ловим BadRequest, если available=null ")
    public void shouldItemWithoutAvailable() throws Exception {
        Item item = new Item(2L, "Дрель", "Простая дрель", user, null, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Ловим BadRequest, если name пустое")
    public void shouldItemWitEmptyName() throws Exception {
        Item item = new Item(2L, "", "Простая дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Ловим BadRequest, если name=null")
    public void shouldCreateItemWithEmptyName() throws Exception {
        Long userId = 1L;

        Item item = new Item(2L, null, "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Ловим BadRequest, если description пустое")
    public void shouldItemWitEmptyDescription() throws Exception {
        Item item = new Item(2L, "Дрель", "", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Обновляем item")
    public void shouldItemUpdate() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        Item newItem = new Item(1L, "Дрель+", "Аккумуляторная дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(newItem);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель+"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    @DisplayName("Ловим BadRequest, если обновляем item без X-Sharer-User-Id")
    public void shouldItemUpdateWithoutXSharerUserId() throws Exception {
        Item newItem = new Item(1L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(newItem);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Ловим NotFound, если обновляем item для не существующего user")
    public void shouldItemUpdateWithUnknownUser() throws Exception {
        Item newItem = new Item(1L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(newItem);
        Long userId = 999L;

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Обновляем item полем available")
    public void shouldItemUpdateAvailable() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        Map<Object, Object> newInput = new HashMap<>();
        newInput.put("available", false);
        String jsonItem = objectMapper.writeValueAsString(newInput);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value("false"));
    }

    @Test
    @DisplayName("Обновляем item полем description")
    public void shouldItemUpdateDescription() throws Exception {
        String jsonItem = "{\"description\":\"Аккумуляторная дрель + аккумулятор\"}";
        Long userId = 1L;

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель + аккумулятор"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    @DisplayName("Обновляем item полем name")
    public void shouldItemUpdateName() throws Exception {
        String jsonItem = "{\"name\":\"Аккумуляторная дрель\"}";
        Long userId = 1L;

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(jsonPath("$.name").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    @DisplayName("Добавляем новый item")
    public void shouldGetItemByHeader() throws Exception {
        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        User userNew = new User(2L, "userNew", "userNew@userNew.com");
        String jsonUser = objectMapper.writeValueAsString(userNew);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));

        Long userIdNew = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userIdNew)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userIdNew))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Дрель+"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[0].available").value("false"));

    }

    @Test
    @DisplayName("Ищем item по тексту")
    public void shouldSearchItemByText() throws Exception {
        Long userId = 1L;
        String text = "аккУМУляторная";

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());


        mockMvc.perform(get("/items/search?text=" + text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Дрель+"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[0].available").value("true"));

    }

    @Test
    @DisplayName("Получаеи 2 items на запрос по тексту")
    public void shouldSearchItemByTextForTwoItem() throws Exception {
        Long userId = 1L;
        String text = "дРелЬ";

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());


        mockMvc.perform(get("/items/search?text=" + text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Дрель+"))
                .andExpect(jsonPath("$[1].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[1].available").value("true"));

    }

    @Test
    @DisplayName("Получаем пучтой список, если текст пустой")
    public void shouldSearchItemByTextWhenEmpty() throws Exception {
        Long userId = 1L;
        String text = "";

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());


        mockMvc.perform(get("/items/search?text=" + text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    @DisplayName("Удаляем item")
    public void shouldDeleteItem() throws Exception {
        Long userId = 1L;

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);


        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/items/{id}", 2L)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/{id}", 2L)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

}