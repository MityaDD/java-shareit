package ru.practicum.server.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.server.booking.storage.BookingStorage;
import ru.practicum.server.item.storage.ItemStorage;
import ru.practicum.server.user.storage.UserStorage;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.storage.CommentStorage;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private CommentStorage commentStorage;

    final LocalDateTime timeNow = LocalDateTime.now().withNano(0);
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = userStorage.save(new User(1L, "user", "user@user.com"));
        item = itemStorage.save(new Item(1L, "item", "description", user, true, null));
    }

    @AfterEach
    void reset() {
        userStorage.deleteAll();
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
        commentStorage.deleteAll();
    }

    @Test
    @DisplayName("findAllByItemId")
    void findAllByItemId() {
        final Comment comment1 = commentStorage.save(new Comment(
                1L, "1 text", item, user.getName(), timeNow));
        final Comment comment2 = commentStorage.save(new Comment(
                2L, "2 text", item, user.getName(), timeNow.plusDays(1)));

        List<Comment> result = commentStorage.findAllByItemId(item.getId());

        assertThat(result, notNullValue());
        assertEquals(2, result.size());
        assertThat(result, hasItem(comment1));
        assertThat(result, hasItem(comment2));
    }

    @Test
    @DisplayName("findAllByAndAuthorName")
    void findAllByAndAuthorName() {
        final Comment comment1 = commentStorage.save(new Comment(
                1L, "1 text", item, user.getName(), timeNow));
        final Comment comment2 = commentStorage.save(new Comment(
                2L, "2 text", item, user.getName(), timeNow.plusDays(1)));

        List<Comment> result = commentStorage.findAllByAndAuthorName(user.getName());

        assertThat(result, notNullValue());
        assertEquals(2, result.size());
        assertThat(result, hasItem(comment1));
        assertThat(result, hasItem(comment2));
    }
}
