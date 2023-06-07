package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestResponseDto addRequest(@RequestHeader(HEADER) @NotNull Long userId,
                                             @Valid @RequestBody ItemRequestDto dto) {
        return requestService.addRequest(dto, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllRequestsByOwner(@RequestHeader(HEADER) @NotNull Long userId) {
        return requestService.getAllRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequestsByOtherUsers(@RequestHeader(HEADER) @NotNull Long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        return requestService.getAllRequestsByOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(HEADER) @NotNull Long userId,
                                                 @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}