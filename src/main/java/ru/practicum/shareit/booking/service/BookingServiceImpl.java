package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Log;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingStorage bookingStorage;
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findBookingOwnerOrBooker(bookingId, userId);
        if (booking == null) {
            Log.andThrowNotFound("Booking не найден");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto addBooking(Long bookerId, BookingDtoInput dto) {
        log.info("Запрошена бронь пользователем с id={}, на предмет {}", bookerId, dto);
        User booker = userService.getById(bookerId);
        Item item = itemService.getById(dto.getItemId());
        validateAddBooking(bookerId, dto, item);
        Booking booking = BookingMapper.toBooking(dto, item, booker);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        Booking bookingSaved = bookingStorage.save(booking);
        log.debug("сохранен предмет: {}", bookingSaved);
        return BookingMapper.toBookingDto(bookingSaved);
    }

    @Transactional
    @Override
    public BookingDto setApprovedByOwner(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingStorage.findBookingOwner(bookingId, userId);
        if (booking == null) {
            throw new NotFoundException("Booking не найден");
        }
        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                Log.andThrowNotValid(String.format("У бронирования с id=%d уже стоит статус APPROVED", bookingId));
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking bookingSaved = bookingStorage.save(booking);
        return BookingMapper.toBookingDto(bookingSaved);
    }

    @Transactional
    @Override
    public List<BookingDto> getAllBookings(String state, Long userId, String typeUser) {
        log.info("Запрошен список пользователя с id={} и стейтом={}", userId, typeUser);
        if (state == null) {
            state = "ALL";
        }
        List<Booking> list = getBookingsList(state, userId, typeUser);
        log.info("Получен список: {}", list);
        if (list.isEmpty()) {
            Log.andThrowNotFound("Бронирование не найдено");
        }
        return list.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsList(String state, Long userId, String typeUser) {
        LocalDateTime time = LocalDateTime.now();
        String criteria = typeUser + state;
        switch (criteria) {
            case "ownerALL":
                return bookingStorage.findAllByOwnerIdOrderByStartDesc(userId);
            case "bookerALL":
                return bookingStorage.findAllByBookerIdOrderByStartDesc(userId);
            case "ownerFUTURE":
                return bookingStorage.findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, time);
            case "bookerFUTURE":
                return bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, time);
            case "ownerWAITING":
                return bookingStorage.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case "bookerWAITING":
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case "ownerCURRENT":
                return bookingStorage.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time);
            case "bookerCURRENT":
                return bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time);
            case "ownerPAST":
                return bookingStorage.findAllByOwnerIdAndEndBeforeOrderByStartDesc(userId, time);
            case "bookerPAST":
                return bookingStorage.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, time);
            case "ownerREJECTED":
                return bookingStorage.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            case "bookerREJECTED":
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default:
                throw new NotValidException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validateAddBooking(long bookerId, BookingDtoInput bookingDtoInput, Item item) {
        if (isNotValidDate(bookingDtoInput.getStart(), bookingDtoInput.getEnd())) {
            Log.andThrowNotValid("Даты бронирования выбраны некорректно." + bookingDtoInput);
        }
        if (bookerId == item.getOwner().getId()) {
            Log.andThrowNotFound("Владелец вещи не может бронировать свои вещи.");//
        }
        if (!item.getAvailable()) {
            Log.andThrowNotValid(String.format("Вещь с id=%d не доступна для бронирования.", item.getId()));
        }
    }

    private boolean isNotValidDate(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(NOW) || end.isBefore(NOW) || end.isBefore(start) || start.equals(end);
    }
}
