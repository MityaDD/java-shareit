package ru.practicum.server.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.booking.storage.BookingStorage;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.storage.ItemStorage;
import ru.practicum.server.request.storage.ItemRequestStorage;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.storage.UserStorage;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private ItemRequestStorage requestStorage;
    private User user;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        user = userStorage.save(new User(1L, "user", "user@user.com"));
        item1 = itemStorage.save(new Item(1L, "drill", "push me", user,true,  null));
        item2 = itemStorage.save(new Item(2L, "mirror", "on the wall", user,true,  null));

    }

    @AfterEach
    void reset() {
        userStorage.deleteAll();
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
    }

    @Test
    @DisplayName("findAllByOwnerOrderById")
    void findAllByOwnerOrderById() {
        List<Item> result = itemStorage.findAllByOwnerOrderById(user);

        assertThat(result, notNullValue());
        assertEquals(2, result.size());
        assertThat(result, hasItem(item1));
        assertThat(result, hasItem(item2));
    }

    @Test
    @DisplayName("findByNameContaining")
    void findByNameContaining() {
        List<Item> result = itemStorage.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable("mir", "push", true);

        assertThat(result, notNullValue());
        assertEquals(2, result.size());
        assertThat(result, hasItem(item2));
        assertThat(result, hasItem(item1));
    }

}