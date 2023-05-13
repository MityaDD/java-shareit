package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItemById(Long id) {
        log.info("Запрошен предмет с id={}", id);
        validateItem(itemStorage.getItemById(id));
        return itemMapper.toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        log.info("Запрошен список предметов владельца с id={}", userId);
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
        log.info("Добавлен новый предмет: {}", addedItem);
        return itemMapper.toItemDto(addedItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item newItem = itemMapper.toItem(itemDto);
        validateUser(userId);
        Item oldItem = itemStorage.getItemById(itemId);
        validateItemOwner(oldItem, userId);
        checkAndSetFields(newItem, oldItem);
        Item updatedItem = itemStorage.updateItem(oldItem);
        log.debug("Обновлен предмет: {}", updatedItem);
        return itemMapper.toItemDto(updatedItem);
    }

    public void deleteItem(Long id) {
        validateItem(itemStorage.getItemById(id));
        itemStorage.deleteItem(id);
        log.debug("Удален предмет c id={}", id);
    }

    @Override
    public Collection<ItemDto> searchItemsByDescription(String text) {
        log.info("Запрошен список по описанию: {}", text);
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

    private void validateUser(Long userId) {
        if (!userStorage.getAllUsers().contains(userStorage.getUserById(userId))) {
            Log.andThrowNotFound("Не найден пользователь c id " + userId);
        }
    }

    private void validateItemOwner(Item item, Long userId) {
        if (item.getOwner().getId() != userId) {
            Log.andThrowNotFound("Пользователь не владеет этой вещью!");
        }
    }

    private void checkAndSetFields(Item newItem, Item oldItem) {
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
    }


}