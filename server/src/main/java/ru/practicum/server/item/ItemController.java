package ru.practicum.server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemResponseDto;
import ru.practicum.server.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping()
    public ItemDto createItem(@RequestHeader(HEADER) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@PathVariable Long itemId, @RequestHeader(HEADER) @NotNull Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        return itemService.searchItemsByDescription(text);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping()
    public List<ItemResponseDto> findAll(@RequestHeader(HEADER) Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentByItemId(@RequestHeader(HEADER) @NotNull Long userId,
                                         @PathVariable("itemId") @Positive Long itemId,
                                         @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
