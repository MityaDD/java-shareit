package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;

import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
class BookingStorageTest {
    private final LocalDateTime timeNow = LocalDateTime.now();
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private BookingStorage bookingStorage;
    private User user;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;

    @BeforeEach
    void setUp() {
        user = userStorage.save(new User(1L, "user", "user@user.com"));
        Item item = itemStorage.save(new Item(
                1L, "item1", "1 description", user, true, null)
        );

        booking1 = bookingStorage.save(new Booking(
                1L, item, timeNow, timeNow.plusDays(2), user, Status.APPROVED));
        booking2 = bookingStorage.save(new Booking(
                2L, item, timeNow.minusDays(10), timeNow.minusDays(7), user, Status.WAITING));
        booking3 = bookingStorage.save(new Booking(
                3L, item, timeNow.minusDays(2), timeNow.plusDays(2), user, Status.REJECTED));
        booking4 = bookingStorage.save(new Booking(
                4L, item, timeNow.plusDays(3), timeNow.plusDays(8), user, Status.APPROVED));
    }

    @AfterEach
    void reset() {
        userStorage.deleteAll();
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
    }

    @Test
    @DisplayName("ownerALL APPROVED")
    void test_6_findAllByOwnerIdOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByOwnerIdOrderByStartDesc(
                user.getId(), PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking1));
        assertThat(result, hasItem(booking4));
    }

    @Test
    @DisplayName("bookerALL APPROVED")
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByBookerIdOrderByStartDesc(
                user.getId(), PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking1));
        assertThat(result, hasItem(booking4));
    }

    @Test
    @DisplayName("ownerFUTURE")
    void findAllByOwnerIdAndStartAfterOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByOwnerIdAndStartAfterOrderByStartDesc(
                user.getId(), timeNow, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking4));
    }

    @Test
    @DisplayName("bookerFUTURE")
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(
                user.getId(), timeNow, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking4));
    }

    @Test
    @DisplayName("ownerCURRENT")
    void findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user.getId(), timeNow, timeNow, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking3));
    }

    @Test
    @DisplayName("bookerCURRENT")
    void test_2_findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user.getId(), timeNow, timeNow, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking3));
    }

    @Test
    @DisplayName("ownerPAST")
    void test_8_findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByOwnerIdAndEndBeforeOrderByStartDesc(
                user.getId(), timeNow, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking2));
    }

    @Test
    @DisplayName("bookerPAST")
    void test_3_findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                user.getId(), timeNow, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking2));
    }

    @Test
    @DisplayName("ownerWAITING")
    void test_10_findAllByItem_Owner_IdAndStatusOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByOwnerIdAndStatusOrderByStartDesc(
                user.getId(), Status.WAITING, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking2));
    }

    @Test
    @DisplayName("bookerWAITING")
    void test_5_findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(
                user.getId(), Status.WAITING, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking2));
    }

    @Test
    @DisplayName("ownerREJECTED")
    void findAllByOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByOwnerIdAndStatusOrderByStartDesc(
                user.getId(), Status.REJECTED, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking3));
    }

    @Test
    @DisplayName("bookerREJECTED")
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(
                user.getId(), Status.REJECTED, PageRequest.of(0,2));

        assertThat(result, notNullValue());
        assertThat(result, hasItem(booking3));
    }

}