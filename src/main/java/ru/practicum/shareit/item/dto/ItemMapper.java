package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoSpecial;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Comparator;
import java.util.List;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        if (itemDto == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static ItemResponseDto toItemResponseDto(Item item, List<Booking> booking, List<Comment> comment) {
        BookingDtoSpecial bookingLast = null;
        BookingDtoSpecial bookingNext = null;
        LocalDateTime time = LocalDateTime.now();

        if (!booking.isEmpty()) {
            Optional<Booking> bookingLastOld = booking.stream()
                    .filter(b -> (b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED)))
                    .filter(b -> (b.getStart().isBefore(time) && b.getEnd().isAfter(time)) || b.getEnd().isBefore(time))
                    .sorted(Comparator.comparing(Booking::getId).reversed())
                    .findFirst();

            Optional<Booking> bookingNextOld = booking.stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .filter(b -> b.getStart().isAfter(time))
                    .findFirst();
            if (bookingLastOld.isPresent()) {
                bookingLast = BookingMapper.toBookingDtoForItem(bookingLastOld.get());
            }
            if (bookingNextOld.isPresent()) {
                bookingNext = BookingMapper.toBookingDtoForItem(bookingNextOld.get());
            }

        }
        return ItemResponseDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .lastBooking(bookingLast)
                .nextBooking(bookingNext)
                .comments(comment)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment, User author) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .authorName(author.getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }

    public static Comment toComment(CommentDto dto, User author, Item item) {
        return Comment.builder()
                .authorName(author.getName())
                .created(LocalDateTime.now())
                .text(dto.getText())
                .item(item)
                .build();
    }

    /*
    public static CommentDto toCommentDto(Comment comment, User author) {
        if (comment == null) {
            return null;
        }
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(author.getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Comment toComment(CommentDto dto, User author, Item item) {
        if (dto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setText(dto.getText());
        comment.setAuthorName(author.getName());
        comment.setItem(item);
        comment.setCreated(dto.getCreated());
        return comment;
    } */
}