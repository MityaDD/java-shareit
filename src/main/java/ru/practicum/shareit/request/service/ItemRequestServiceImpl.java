package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage requestStorage;
    private final UserService userService;
    private final ItemStorage itemStorage;

    public ItemRequestResponseDto addRequest(ItemRequestDto dto, Long userId) {
        userService.getById(userId);
        ItemRequest savedRequest = requestStorage.save(ItemRequestMapper.toItemRequest(dto, userId));
        log.info("Добавлен нновый риквест:{} пользователя с id={}",dto, userId);
        return ItemRequestMapper.toItemRequestResponseDto(savedRequest, new ArrayList<>());
    }

    public List<ItemRequestResponseDto> getAllRequestsByOwner(Long userId) {
        log.info("Запрошен список риквестов пользователя с id={}", userId);
        userService.getById(userId);
        List<ItemRequest> itemRequests = requestStorage.findAllByRequesterOrderByCreatedDesc(userId);
        return getItemRequestResponseDto(itemRequests);
    }

    public List<ItemRequestResponseDto> getAllRequestsByOtherUsers(Long userId, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            Log.andThrowNotValid("size или from должен быть больше 0");
        }
        userService.getById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequests = requestStorage.findAllByRequesterIsNotOrderByCreatedDesc(userId, page);
        return getItemRequestResponseDto(itemRequests);
    }

    private List<ItemRequestResponseDto> getItemRequestResponseDto(List<ItemRequest> itemRequests) {
        List<Item> items = itemStorage.findAllByRequestIdIn(
                itemRequests.stream()
                        .map(ItemRequest::getId)
                        .collect(Collectors.toList()));


        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestResponseDto(
                        itemRequest, items.stream()
                                .filter(item -> item.getRequestId().equals(itemRequest.getId()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public ItemRequestResponseDto getRequestById(Long userId, Long requestId) {
        userService.getById(userId);
        ItemRequest itemRequest = requestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<Item> items = itemStorage.findAllByRequestIdIn(List.of(itemRequest.getId()));
        return ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
    }
}

