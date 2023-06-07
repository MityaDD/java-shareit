package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage requestStorage;
    private final UserService userService;
    private final ItemStorage itemStorage;

    @Transactional
    @Override
    public ItemRequestDto addRequest(ItemRequestDtoInput dto, Long userId) {
        userService.getById(userId);
        ItemRequest savedRequest = requestStorage.save(ItemRequestMapper.toItemRequest(dto, userId));
        log.info("Добавлен нновый риквест:{} пользователя с id={}", dto, userId);
        return ItemRequestMapper.toItemRequestDto(savedRequest, new ArrayList<>());
    }

    @Transactional
    @Override
    public List<ItemRequestDto> getAllRequestsByOwner(Long userId) {
        log.info("Запрошен список риквестов пользователя с id={}", userId);
        userService.getById(userId);
        List<ItemRequest> itemRequests = requestStorage.findAllByRequesterOrderByCreatedDesc(userId);
        return getItemRequestDto(itemRequests);
    }

    @Transactional
    @Override
    public List<ItemRequestDto> getAllRequestsByOtherUsers(Long userId, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            Log.andThrowNotValid("size или from должен быть больше 0");
        }
        userService.getById(userId);
        log.info("Запрошен список всех риквестов для пользователя с id={}", userId);
        PageRequest pages = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequests = requestStorage.findAllByRequesterIsNotOrderByCreatedDesc(userId, pages);
        return getItemRequestDto(itemRequests);
    }

    @Transactional
    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Запрошен риквест с id={} пользователя с id={}", requestId, userId);
        userService.getById(userId);
        ItemRequest itemRequest = getById(requestId);
        List<Item> items = itemStorage.findAllByRequestIdIn(List.of(itemRequest.getId()));
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    private ItemRequest getById(Long requestId) {
        Optional<ItemRequest> itemRequest = requestStorage.findById(requestId);
        if (!itemRequest.isPresent()) {
            Log.andThrowNotFound(String.format("Запрос с id=%s не найден.", requestId));
        }
        return itemRequest.get();
    }

    private List<ItemRequestDto> getItemRequestDto(List<ItemRequest> itemRequests) {
        List<Item> items = itemStorage.findAllByRequestIdIn(
                itemRequests.stream()
                        .map(ItemRequest::getId)
                        .collect(Collectors.toList()));
        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(
                        itemRequest, items.stream()
                                .filter(item -> item.getRequestId().equals(itemRequest.getId()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}

