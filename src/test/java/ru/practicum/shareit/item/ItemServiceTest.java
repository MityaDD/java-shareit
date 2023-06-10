package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingDtoSpecial;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceTest {

    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final UserStorage userStorage;
    private final ItemService itemService;

    @AfterEach
    void reset() {
        userStorage.deleteAll();
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
        commentStorage.deleteAll();
    }

    @Test
    @DisplayName("Добавляем новый item")
    public void shouldAddItem() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");

        userStorage.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        Long userId = user.getId();

        ItemDto result = itemService.addItem(userId, itemDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(result.getId(), 1L);
        assertEquals(result.getName(), "Test Item");
        assertEquals(result.getDescription(), "Test Description");

    }

    @Test
    @DisplayName("Получаем все item")
    public void shouldGetAllItems() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        userStorage.save(user);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Test Description");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemStorage.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Test Description 2");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemStorage.save(item2);

        Long userId = user.getId();

        List<ItemResponseDto> itemResponseDtoList = itemService.getAllItemsByUserId(userId);

        assertNotNull(itemResponseDtoList);
        assertEquals(2, itemResponseDtoList.size());
    }

    @Test
    @DisplayName("Получаем item по id")
    public void shouldGetItemById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        userStorage.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemStorage.save(item);

        Long itemId = item.getId();
        Long userId = user.getId();

        ItemResponseDto itemResponseDto = itemService.getItemById(itemId, userId);

        assertNotNull(itemResponseDto);
    }

    @Test
    @DisplayName("Обновляем item")
    public void shouldUpdateItem() {
        User user = new User(1L,"Test User", "test@example.com" );
        userStorage.save(user);

        Item item = new Item();
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemStorage.save(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("UpdatedName");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);

        Long itemId = item.getId();
        Long userId = user.getId();

        ItemDto result = itemService.updateItem(userId, itemId, itemDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(result.getId(), 1L);
        assertEquals(result.getName(), "UpdatedName");
        assertEquals(result.getDescription(), "Updated Description");
    }

    @Test
    @DisplayName("Удаляем item по id")
    public void shouldDeleteItemById() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");

        item.setAvailable(true);
        itemStorage.save(item);

        Long itemId = item.getId();

        itemService.deleteItem(itemId);

        assertFalse(itemStorage.existsById(itemId));
    }

    @Test
    @DisplayName("Получаем список по тегу")
    public void shouldSearchItemsByKeyWord() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userStorage.save(user);

        Item item1 = new Item();
        item1.setName("Test Item 1");
        item1.setDescription("Description item 1");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemStorage.save(item1);

        Item item2 = new Item();
        item2.setName("Test Item 2");
        item2.setDescription("Description item 2");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemStorage.save(item2);

        Item item3 = new Item();
        item3.setName("Another Item");
        item3.setDescription("Description item");
        item3.setAvailable(false);
        item3.setOwner(user);
        itemStorage.save(item3);

        Collection<ItemDto> result = itemService.searchItemsByDescription("test");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Test Item 1")));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Test Item 2")));
    }



    @Test
    @DisplayName("Тест полей ItemResponseDto")
    public void testItemResponseDto() {
        Long id = 1L;
        String name = "Test Item";
        String description = "Test Description";
        User owner = new User(2L, "Test Owner","owner@ro.ru");

        Boolean available = true;
        Long requestId = 3L;
        BookingDtoSpecial lastBooking = new BookingDtoSpecial();
        BookingDtoSpecial nextBooking = new BookingDtoSpecial();
        List<Comment> comments = new ArrayList<>();

        ItemResponseDto itemResponseDto = new ItemResponseDto(id, name, description, owner, available, lastBooking, nextBooking, comments, requestId);

        assertEquals(id, itemResponseDto.getId());
        assertEquals(name, itemResponseDto.getName());
        assertEquals(description, itemResponseDto.getDescription());
        assertEquals(owner, itemResponseDto.getOwner());
        assertEquals(available, itemResponseDto.getAvailable());
        assertEquals(requestId, itemResponseDto.getRequestId());
        assertEquals(lastBooking, itemResponseDto.getLastBooking());
        assertEquals(nextBooking, itemResponseDto.getNextBooking());
        assertEquals(comments, itemResponseDto.getComments());
    }


    @Test
    @DisplayName("Тест полей ItemDto")
    public void testItem() {
        User owner = new User(2L, "Test Owner","owner@ro.ru");
        Long id = 1L;
        String name = "Test Item";
        String description = "Test Description";
        Boolean available = true;
        Long requestId = 2L;

        ItemDto itemDto = new ItemDto(id, name, description, owner, available, requestId);

        assertEquals(id, itemDto.getId());
        assertEquals(name, itemDto.getName());
        assertEquals(description, itemDto.getDescription());
        assertEquals(available, itemDto.getAvailable());
        assertEquals(requestId, itemDto.getRequestId());
    }

    @Test
    @DisplayName("Тест маппинга с bookingDtoSpecial")
    public void testToItemResponseDto() {
        Long itemId = 1L;
        String itemName = "Test Item";
        String itemDescription = "Test Description";
        Long ownerId = 2L;
        String ownerName = "Test Owner";
        Boolean itemAvailable = true;
        Long requestId = 3L;

        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDescription);
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);
        item.setOwner(owner);
        item.setAvailable(itemAvailable);
        item.setRequestId(requestId);

        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking();
        booking1.setId(4L);
        booking1.setItem(item);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(1));
        booking1.setBooker(new User(3L, "Ivan", "ivan@mail.ru"));
        bookings.add(booking1);
        Booking booking2 = new Booking();
        booking2.setId(5L);
        booking2.setItem(item);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setBooker(new User(3L, "Petr", "petr@mail.ru"));
        bookings.add(booking2);

        List<Comment> comments = new ArrayList<>();

        User owner2 = new User(2L, "Test Owner","owner@ro.ru");

        BookingDtoSpecial bookingLastDto = new BookingDtoSpecial();
        bookingLastDto.setId(booking1.getId());

        BookingDtoSpecial bookingNextDto = new BookingDtoSpecial();
        bookingNextDto.setId(booking2.getId());

        ItemResponseDto expectedDto = new ItemResponseDto();
        expectedDto.setId(itemId);
        expectedDto.setName(itemName);
        expectedDto.setDescription(itemDescription);
        expectedDto.setOwner(owner2);
        expectedDto.setAvailable(itemAvailable);
        expectedDto.setLastBooking(bookingLastDto);
        expectedDto.setNextBooking(bookingNextDto);
        expectedDto.setComments(comments);
        expectedDto.setRequestId(requestId);

        ItemResponseDto actualDto = ItemMapper.toItemResponseDto(item, bookings, comments);

        assertEquals(expectedDto.getId(), actualDto.getId());
        assertEquals(expectedDto.getName(), actualDto.getName());
        assertEquals(expectedDto.getDescription(), actualDto.getDescription());
    }

    @Test
    @DisplayName("Добавляем comment")
    public void shouldAddComment() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userStorage.save(user);

        User booker = new User();
        booker.setEmail("booker@booker.com");
        booker.setName("booker User");
        userStorage.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description item 1");
        item.setAvailable(true);
        item.setOwner(user);
        Item savedItem = itemStorage.save(item);

        BookingDtoInput bookingDto = new BookingDtoInput();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingStorage.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        CommentDto result = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertNotNull(result.getId());
        assertEquals(result.getText(), "Test Comment");
        assertEquals(result.getAuthorName(), "booker User");

        result.setItem(new Item());
        assertNotNull(result.getItem());
    }

}