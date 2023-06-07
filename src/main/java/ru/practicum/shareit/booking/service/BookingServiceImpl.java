package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.storage.BookingStorage;
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
            Log.andThrowNotFound("Booking не найден");
        }
        if (approved) {
            if (booking.getStatus() == Status.APPROVED) {
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
    public List<BookingDto> getAllBookings(String state, Long userId, String typeUser, int from, int size) {
        log.info("Запрошен список пользователя с id={} и стейтом={}", userId, typeUser);
        if (state == null) {
            state = "ALL";
        }
        if (size <= 0 || from < 0) {
            Log.andThrowNotValid("size и from должны быть больше 0"); ///////валид
        }
        PageRequest pages = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Booking> list = getBookingsList(state, userId, typeUser, pages);
        log.info("Получен список: {}", list);
        if (list.isEmpty()) {
            Log.andThrowNotFound("Бронирование не найдено");
        }
        return list.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsList(String state, Long userId, String typeUser, Pageable pages) {
        LocalDateTime time = LocalDateTime.now();
        String criteria = typeUser + state;
        switch (criteria) {
            case "ownerALL":
                return bookingStorage.findAllByOwnerIdOrderByStartDesc(userId, pages);
            case "bookerALL":
                return bookingStorage.findAllByBookerIdOrderByStartDesc(userId, pages);
            case "ownerFUTURE":
                return bookingStorage.findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, time, pages);
            case "bookerFUTURE":
                return bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, time, pages);
            case "ownerWAITING":
                return bookingStorage.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pages);
            case "bookerWAITING":
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pages);
            case "ownerCURRENT":
                return bookingStorage.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time, pages);
            case "bookerCURRENT":
                return bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time, pages);
            case "ownerPAST":
                return bookingStorage.findAllByOwnerIdAndEndBeforeOrderByStartDesc(userId, time, pages);
            case "bookerPAST":
                return bookingStorage.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, time, pages);
            case "ownerREJECTED":
                return bookingStorage.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pages);
            case "bookerREJECTED":
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pages);
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
        LocalDateTime now = LocalDateTime.now();
        return start.isBefore(now) || end.isBefore(now) || end.isBefore(start) || start.equals(end);
    }
}
