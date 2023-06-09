package ru.practicum.server.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.booking.dto.BookingDtoInput;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.service.BookingService;

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
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookings(state, userId, "booker", from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllReservationsByOwnerId(@RequestHeader(HEADER) Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookings(state, userId, "owner", from, size);
    }
}

