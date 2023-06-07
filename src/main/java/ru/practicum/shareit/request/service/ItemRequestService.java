package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto addRequest(ItemRequestDto dto, Long userId);
    List<ItemRequestResponseDto> getAllRequestsByOwner(Long userId);
    List<ItemRequestResponseDto> getAllRequestsByOtherUsers(Long userId, Integer from, Integer size);

    public ItemRequestResponseDto getRequestById(Long userId, Long requestId);
}
