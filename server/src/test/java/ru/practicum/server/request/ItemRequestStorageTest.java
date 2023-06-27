package ru.practicum.server.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.server.item.storage.ItemStorage;
import ru.practicum.server.request.dto.ItemRequestDtoInput;
import ru.practicum.server.request.dto.ItemRequestMapper;
import ru.practicum.server.request.storage.ItemRequestStorage;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private ItemRequestStorage requestStorage;
    private User user;
    private User requestor;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        final LocalDateTime timeNow = LocalDateTime.now();

        user = userStorage.save(new User(2L, "user", "user@user.com"));
        requestor = userStorage.save(new User(1L, "requestor", "requestor@urequestor.com"));

        ItemRequestDtoInput dtoInput = new ItemRequestDtoInput("1 description");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(dtoInput, requestor.getId());

        ItemRequestDtoInput dtoInput2 = new ItemRequestDtoInput("2 description");
        ItemRequest itemRequestTwo = ItemRequestMapper.toItemRequest(dtoInput2, requestor.getId());

        itemRequest1 = requestStorage.save(itemRequest);
        itemRequest2 = requestStorage.save(itemRequestTwo);

    }

    @AfterEach
    void reset() {
        userStorage.deleteAll();
        itemStorage.deleteAll();
        requestStorage.deleteAll();
        user = null;
        requestor = null;
        itemRequest1 = null;
        itemRequest2 = null;
    }

    @Test
    @DisplayName("тест 1")
    void findAllByRequester() {
        List<ItemRequest> result = requestStorage.findAllByRequesterOrderByCreatedDesc(user.getId());

        assertThat(result, notNullValue());
        assertEquals(2, result.size());
        assertThat(result, hasItem(itemRequest1));
        assertThat(result, hasItem(itemRequest2));
    }

    @Test
    @DisplayName("тест 2")
    void findAllByRequesterId() {
        Long id = itemRequest2.getId();

        List<ItemRequest> result = requestStorage.findAllByRequesterIsNotOrderByCreatedDesc(id, PageRequest.of(1,1));

        assertThat(result, notNullValue());
        assertEquals(1, result.size());
        assertThat(result, hasItem(itemRequest1));
    }

    @Test
    @DisplayName("тест 3")
    void findAllItemRequestByOtherUser() {
        List<ItemRequest> result = requestStorage.findAllByRequesterIsNotOrderByCreatedDesc(100L, PageRequest.of(1,1));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(itemRequest1));
    }

}
