package ru.practicum.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.dto.BookingDtoSpecial;
import ru.practicum.server.user.model.User;
import ru.practicum.server.item.model.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private User owner;
    @NotNull
    private Boolean available;
    private BookingDtoSpecial lastBooking;
    private BookingDtoSpecial nextBooking;
    private List<Comment> comments;
    private Long requestId;
}