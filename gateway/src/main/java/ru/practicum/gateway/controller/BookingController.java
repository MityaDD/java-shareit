package ru.practicum.gateway.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.dto.BookingDtoInput;
import ru.practicum.gateway.client.BookingClient;
import ru.practicum.gateway.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import ru.practicum.gateway.exception.UnsupportedStateException;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {
    final BookingClient client;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addReservation(@RequestHeader(HEADER) @Min(1) Long userId,
                                          @Valid @RequestBody BookingDtoInput bookingDtoInput) {
        BookingDtoInput.validate(bookingDtoInput);
        return client.createBooking(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApprovedByOwner(@PathVariable @Min(1) Long bookingId,
                                              @RequestParam boolean approved,
                                              @RequestHeader(HEADER) @Min(1) Long userId) {
        return client.setApprovedByOwner(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable @Min(1) Long bookingId,
                                          @RequestHeader(HEADER) @Min(1) Long userId) {
        return client.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllReservationsByUserId(@RequestHeader(HEADER) @Min(1) Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL") String stateString,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {
        State state = State.from(stateString)
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateString));
        return client.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllReservationsByOwnerId(@RequestHeader(HEADER) @Min(1) Long userId,
                                                       @RequestParam(value = "state", defaultValue = "ALL") String stateString,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        State state = State.from(stateString)
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateString));
        return client.getAllBookingsForOwner(userId, state,  from, size);
    }
}
