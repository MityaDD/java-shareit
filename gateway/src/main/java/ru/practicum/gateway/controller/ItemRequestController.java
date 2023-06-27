package ru.practicum.gateway.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import ru.practicum.gateway.client.RequestClient;
import ru.practicum.gateway.dto.ItemRequestDtoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";
    final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody ItemRequestDtoInput dto,
                                      @RequestHeader(HEADER) @NotNull Long userId) {
        return client.createRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByOwner(@RequestHeader(HEADER) @NotNull Long userId) {
        return client.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsByOtherUsers(@RequestHeader(HEADER) @NotNull Long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {
        return client.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(HEADER) @NotNull Long userId,
                                          @PathVariable Long requestId) {
        return client.getRequestById(userId, requestId);
    }
}
