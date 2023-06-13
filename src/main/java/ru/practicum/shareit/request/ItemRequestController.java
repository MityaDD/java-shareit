package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestDtoInput dto,
                                     @RequestHeader(HEADER) @NotNull Long userId) {
        return requestService.addRequest(dto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByOwner(@RequestHeader(HEADER) @NotNull Long userId) {
        return requestService.getAllRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsByOtherUsers(@RequestHeader(HEADER) @NotNull Long userId,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                           @RequestParam(defaultValue = "10") @Positive int size) {
        return requestService.getAllRequestsByOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(HEADER) @NotNull Long userId,
                                         @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
