package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemResponseDto;
import ru.practicum.server.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Item getById(Long id);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getAllItemsByUserId(Long userId);

    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long id);

    Collection<ItemDto> searchItemsByDescription(String keyword);

    CommentDto addComment(Long bookerId, Long itemId, CommentDto commentDto);
}