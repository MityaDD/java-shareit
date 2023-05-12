package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto getItemById(Long id);

    List<ItemDto> getAllItemsByUserId(Long userId);

    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    void deleteItem(Long id);

    Collection<ItemDto> searchItemsByDescription(String keyword);
}