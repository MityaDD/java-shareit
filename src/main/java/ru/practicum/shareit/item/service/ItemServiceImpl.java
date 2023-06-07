package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Log;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final UserService userService;

    @Override
    public Item getById(Long id) {
        Optional<Item> item = itemStorage.findById(id);
        if (!item.isPresent()) {
            Log.andThrowNotFound(String.format("Предмет с id=%s не найдена.", id));
        }
        return item.get();
    }

    @Transactional
    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        log.info("Запрошен предмет с id={}", itemId);
        Item item = getById(itemId);
        List<Booking> booking = bookingStorage.findAllByItemIdAndOwnerId(itemId, userId);
        List<Comment> comment = commentStorage.findAllByItemId(itemId);
        return ItemMapper.toItemResponseDto(item, booking, comment);
    }

    @Transactional
    @Override
    public List<ItemResponseDto> getAllItemsByUserId(Long userId) {
        log.info("Запрошен список вещей владельца с id={}", userId);
        User user = userService.getById(userId);
        List<Item> itemList = itemStorage.findAllByOwnerOrderById(user);
        List<Long> itemIdList = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> booking = bookingStorage.findAllByOwnerIdAndItemIn(userId, itemIdList);
        List<Comment> comment = commentStorage.findAllByAndAuthorName(user.getName());
        return itemList.stream()
                .map(item -> ItemMapper.toItemResponseDto(item, booking, comment))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userService.getById(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);
        Item addedItem = itemStorage.save(item);
        log.info("Добавлен новый предмет: {}", addedItem);
        return ItemMapper.toItemDto(addedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        Item oldItem = getById(itemId);
        validateItemOwner(oldItem, userId);
        checkAndSetFields(newItemDto, oldItem);
        Item updatedItem = itemStorage.save(oldItem);
        log.debug("Обновлен предмет: {}", updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Transactional
    @Override
    public void deleteItem(Long itemId) {
        Item item = getById(itemId);
        itemStorage.deleteById(item.getId());
        log.debug("Удален предмет c id={}", itemId);
    }

    @Transactional
    @Override
    public Collection<ItemDto> searchItemsByDescription(String text) {
        log.info("Запрошен список по описанию: {}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemStorage.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                text, text, true);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long bookerId, Long itemId, CommentDto commentDto) {
        List<Booking> booking = bookingStorage.findAllByBookerIdAndItemIdAndStatusNotAndStartBefore(bookerId, itemId, Status.REJECTED, LocalDateTime.now());
        if (booking.isEmpty()) {
            Log.andThrowNotValid("Вы не можете оставить отзыв, т.к. не бронировали вещь");
        }
        User author = userService.getById(bookerId);
        Item item = getById(itemId);
        Comment comment = ItemMapper.toComment(commentDto, author, item);
        Comment commentSaved = commentStorage.save(comment);
        log.info("Добавлен новый комментарий к вещи id{}.", itemId);
        return ItemMapper.toCommentDto(commentSaved, author);

    }

    private void validateItemOwner(Item item, long userId) {
        if (item.getOwner().getId() != userId) {
            Log.andThrowNotFound("Пользователь не владеет этой вещью!");
        }
    }

    private void checkAndSetFields(ItemDto itemDto, Item oldItem) {
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
    }

}