package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addReservation(@RequestHeader(HEADER) Long userId,
                                     @Valid @RequestBody BookingDtoInput bookingDtoInput) {
        return bookingService.addBooking(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprovedByOwner(@PathVariable Long bookingId,
                                         @RequestParam("approved") boolean approved,
                                         @RequestHeader(HEADER) Long userId) {
        return bookingService.setApprovedByOwner(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(HEADER) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllReservationsByUserId(@RequestHeader(HEADER) Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookings(state, userId, "booker");
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllReservationsByOwnerId(@RequestHeader(HEADER) Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookings(state, userId, "owner");
    }
}

