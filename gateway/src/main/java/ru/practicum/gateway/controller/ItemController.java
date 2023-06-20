package ru.practicum.gateway.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.client.ItemClient;
import ru.practicum.gateway.dto.ItemDto;
import ru.practicum.gateway.dto.CommentDto;
import ru.practicum.gateway.dto.Validated.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";
    final ItemClient client;

    @PostMapping()
    ResponseEntity<Object> createItem(@RequestHeader(HEADER) @Min(1) Long userId,
                                      @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return client.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    ResponseEntity<Object> getItem(@PathVariable Long itemId, @RequestHeader(HEADER) @Min(1) Long userId) {
        return client.getItemById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    ResponseEntity<Object> updateItem(@RequestHeader(HEADER) @Min(1) Long userId, @PathVariable @Min(1) Long itemId,
                                      @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return client.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    ResponseEntity<Object> searchItems(@RequestParam("text") String text) {
        return client.searchItemsByDescription(text);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable @Min(1) Long itemId) {
        client.deleteItem(itemId);
    }

    @GetMapping()
    ResponseEntity<Object> findAll(@RequestHeader(HEADER) @Min(1) Long userId) {
        return client.getAllItemsByUserId(userId);
    }

    @PostMapping("/{itemId}/comment")
    ResponseEntity<Object> addCommentByItemId(@RequestHeader(HEADER) @NotNull Long userId,
                                         @PathVariable("itemId") @Positive Long itemId,
                                         @Valid @RequestBody CommentDto commentDto) {
        return client.commentItem(userId, itemId, commentDto);
    }
}
