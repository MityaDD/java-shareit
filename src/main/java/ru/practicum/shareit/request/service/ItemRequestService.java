package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDtoInput dto, Long userId);

    List<ItemRequestDto> getAllRequestsByOwner(Long userId);

    List<ItemRequestDto> getAllRequestsByOtherUsers(Long userId, Integer from, Integer size);

    public ItemRequestDto getRequestById(Long userId, Long requestId);

}
