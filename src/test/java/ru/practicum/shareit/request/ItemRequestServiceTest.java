package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import ru.practicum.shareit.item.storage.ItemStorage;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemRequestStorage requestStorage;

    @Test
    @DisplayName("Создаем ItemRequest")
    public void shouldCreateItemRequest() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userStorage.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemStorage.save(item);

        ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();
        itemRequestDtoInput.setDescription("Test message");

        ItemRequestDto itemRequestDto = itemRequestService.addRequest(itemRequestDtoInput, user.getId());

        assertNotNull(itemRequestDto);
        assertNotNull(itemRequestDto.getId());
        assertNotNull(itemRequestDto.getCreated());
        assertNotNull(itemRequestDto.getItems());
        assertEquals(itemRequestDtoInput.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    @DisplayName("Получаем ItemRequest для юзера-риквестера")
    public void shouldGetItemRequestsForUser() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userStorage.save(user);

        User requestor = new User();
        requestor.setEmail("requestor@requestor.com");
        requestor.setName("Test requestor");
        userStorage.save(requestor);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemStorage.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemStorage.save(item2);

        ItemRequestDtoInput itemRequestDtoInput = ItemRequestDtoInput.builder()
                .description("Bla bla bla")
                .build();

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoInput, user.getId());


        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequester(requestor.getId());
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("New item search");
        itemRequest1.setRequester(requestor.getId());
        requestStorage.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequester(requestor.getId());
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("New item search number Two");
        requestStorage.save(itemRequest2);

        List<ItemRequestDto> itemRequestResponseDtos = itemRequestService.getAllRequestsByOwner(requestor.getId());

        assertNotNull(itemRequestResponseDtos);
        assertEquals(2, itemRequestResponseDtos.size());
        assertEquals(2, itemRequest1.getRequester());
    }

    @Test
    @DisplayName("Получаем риквесты других")
    public void shouldGetOtherUsersItemRequests() {

        User user1 = new User();
        user1.setEmail("test1@test.com");
        user1.setName("Test User 1");
        userStorage.save(user1);

        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setName("Test User 2");
        userStorage.save(user2);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        itemStorage.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(user2);
        itemStorage.save(item2);


        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequester(user2.getId());
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("New item search");
        requestStorage.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequester(user2.getId());
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("New item search number Two");
        requestStorage.save(itemRequest2);

        List<ItemRequestDto> itemRequestResponseDtos = itemRequestService.getAllRequestsByOtherUsers(user1.getId(), 0, 10);

        assertNotNull(itemRequestResponseDtos);
        assertNotNull(itemRequestResponseDtos.get(0).getDescription());
        assertEquals(2, itemRequestResponseDtos.size());
    }

    @Test
    @DisplayName("Получаем ItemRequest по id")
    public void shouldGetRequestById() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userStorage.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemStorage.save(item);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user.getId());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("New item search");
        requestStorage.save(itemRequest);

        Item item2 = new Item();
        item2.setId(5L);
        item2.setName("Ivan");
        item2.setDescription("Bla bla vla");
        item2.setAvailable(true);
        item2.setRequestId(1L);

        Item item3 = new Item();
        item3.setId(5L);
        item3.setName("Petr");
        item3.setDescription("Bla bla vla");
        item3.setAvailable(true);
        item3.setRequestId(1L);

        ItemRequestDto itemRequestResponseDto = itemRequestService.getRequestById(user.getId(), itemRequest.getId());
        List<Item> list = List.of(item, item2, item3);
        itemRequestResponseDto.setItems(list);

        assertNotNull(itemRequestResponseDto);
        assertEquals(3, itemRequestResponseDto.getItems().size());
        assertEquals(itemRequest.getId(), itemRequestResponseDto.getId());
    }
}