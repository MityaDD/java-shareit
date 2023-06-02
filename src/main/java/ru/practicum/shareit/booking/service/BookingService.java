package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long bookerId, BookingDtoInput bookingInputDto);

    BookingDto setApprovedByOwner(Long ownerId, Long bookingId, boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookings(String state, Long userId, String typeUser);
}