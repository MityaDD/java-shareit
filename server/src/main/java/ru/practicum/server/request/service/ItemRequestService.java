package ru.practicum.server.request.service;

import ru.practicum.server.request.dto.ItemRequestDtoInput;
import ru.practicum.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDtoInput dto, Long userId);

    List<ItemRequestDto> getAllRequestsByOwner(Long userId);

    List<ItemRequestDto> getAllRequestsByOtherUsers(Long userId, int from, int size);

    public ItemRequestDto getRequestById(Long userId, Long requestId);

}
