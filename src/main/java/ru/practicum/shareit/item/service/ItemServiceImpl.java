package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItemById(Long id) {
        validateItem(itemStorage.getItemById(id));
        return itemMapper.toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemStorage.getAllItems()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item newItem = itemMapper.toItem(itemDto);
        User owner = userStorage.getUserById(userId);
        if (owner == null) {
            Log.andThrowNotFound("Не найден пользователь c id " + userId);
        }
        newItem.setOwner(owner);
        Item addedItem = itemStorage.addItem(newItem);
        return itemMapper.toItemDto(addedItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item  = itemMapper.toItem(itemDto);
        validateUser(userId);
        Item oldItem = itemStorage.getItemById(itemId);

        itemOwnerNameDescAvailValidator(item, oldItem, userId);
        Item updatedItem = itemStorage.updateItem(oldItem);
        return itemMapper.toItemDto(updatedItem);
    }

    public void deleteItem(Long id) {
        validateItem(itemStorage.getItemById(id));
        itemStorage.deleteItem(id);
    }

    @Override
    public Collection<ItemDto> searchItemsByDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getAllItems()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItem(Item item) {
        if (!itemStorage.getAllItems().contains(itemStorage.getItemById(item.getId()))) {
            Log.andThrowNotFound("Не найдена вещь c id " + itemStorage.getItemById(item.getId()));
        }
        if (item.getName().isBlank()) {
            Log.andThrowNotValid("Имя не должно быть пустым");
        }
        if (item.getDescription().isBlank()) {
            Log.andThrowNotValid("Описание не должно быть пустым");
        }
    }
// if (item.getName() != null || item.getName().isBlank()) ====== объединить

    private void validateUser(Long userId) {
        if (!userStorage.getAllUsers().contains(userStorage.getUserById(userId))) {
            Log.andThrowNotFound("Не найден пользователь c id " + userId);
        }
    }

    private void itemOwnerNameDescAvailValidator(Item item, Item oldItem, long userId) {
        if (oldItem.getOwner().getId() != userId) {
            Log.andThrowNotFound("Пользователь не владеет этой вещью!");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
    }


}