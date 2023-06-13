package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingService bookingService;
    BookingDtoInput bookingDtoInput;
    Item item;
    User booker;
    User owner;


    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userStorage.save(owner);

        booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userStorage.save(booker);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemStorage.save(item);

        bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(item.getId());
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(2));
    }

    @AfterEach
    void tearDown() {
        owner = null;
        item = null;
        booker = null;
        bookingDtoInput = null;
    }

    @Test
    @DisplayName("Создает booking")
    public void shouldAddBooking() {
        BookingDto bookingDto = bookingService.addBooking(booker.getId(), bookingDtoInput);

        assertNotNull(bookingDto.getId());
        assertEquals(bookingDto.getStart(), bookingDto.getStart());
        assertEquals(bookingDto.getEnd(), bookingDto.getEnd());
        assertEquals(item.getId(), bookingDto.getItem().getId());
        assertEquals(booker.getId(), bookingDto.getBooker().getId());
    }

    @Test
    @DisplayName("Кидаем exc, если id не найден")
    public void tryGetByIdNotFound() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(owner.getId(), 5L);
        });
    }

    @Test
    @DisplayName("Бросает exc, если booker=owner")
    public void tryCreateBookingForOwner() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(owner.getId(), bookingDtoInput);
        });
    }


    @Test
    @DisplayName("Подтверждаем booking")
    public void shouldSetApproved() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);
        BookingDto bookingResponseDto = bookingService.setApprovedByOwner(owner.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, bookingResponseDto.getStatus());
    }

    @Test
    @DisplayName("Отменяем booking")
    public void shouldSetREJECTED() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);
        BookingDto bookingResponseDto = bookingService.setApprovedByOwner(owner.getId(), booking.getId(), false);

        assertEquals(Status.REJECTED, bookingResponseDto.getStatus());
    }

    @Test
    @DisplayName("Кидаем exc, если booking подтвержден дважды")
    public void trySetApprovedTwice() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);
        BookingDto bookingResponseDto = bookingService.setApprovedByOwner(owner.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, bookingResponseDto.getStatus());
        assertThrows(NotValidException.class, () -> {
            bookingService.setApprovedByOwner(owner.getId(), booking.getId(), true);
        });
    }

    @Test
    @DisplayName("Кидаем exc, если start=end")
    public void trySetApprovedWhenStartEqualEnd() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);
        BookingDto bookingResponseDto = bookingService.setApprovedByOwner(owner.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, bookingResponseDto.getStatus());
        assertThrows(NotValidException.class, () -> {
            bookingService.setApprovedByOwner(owner.getId(), booking.getId(), true);
        });
    }

    @Test
    @DisplayName("Получаем booking по id")
    public void shouldGetById() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);
        BookingDto bookingResponseDto = bookingService.getBookingById(booking.getId(), owner.getId());

        assertNotNull(bookingResponseDto);
        assertEquals(booking.getId(), bookingResponseDto.getId());
    }

    @Test
    @DisplayName("Получаем все booking для owner")
    public void testGetAllReserveForOwner() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);
        List<BookingDto> bookingResponseDtoList = bookingService.getAllBookings("ALL", owner.getId(), "owner", 0, 10);

        assertNotNull(bookingResponseDtoList);
        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    @DisplayName("Кидаем exc, если для не владельца букинга")
    public void testGetAllReserve() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);

        assertThrows(NotFoundException.class, () -> {
            bookingService.getAllBookings("ALL", owner.getId(), "booker", 0, 10);
        });
    }

    @Test
    @DisplayName("Получаем все booking для owner со стейтом Future")
    public void shouldGetAllReserveForOwnerFuture() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);
        bookingStorage.save(booking);

        List<BookingDto> bookingResponseDtoList = bookingService.getAllBookings("ALL", owner.getId(), "owner", 0, 10);

        assertNotNull(bookingResponseDtoList);
        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    @DisplayName("Кидаем exc, если для не владельца букинга стейт Future")
    public void tryGetAllReserveFuture() {
        Booking booking = BookingMapper.toBooking(bookingDtoInput, item, booker);

        bookingStorage.save(booking);

        assertThrows(NotFoundException.class, () -> {
            bookingService.getAllBookings("ALL", owner.getId(), "booker", 0, 10);
        });
    }
}
