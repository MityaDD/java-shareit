package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private int id;
    @NotNull // или проверка?
    private String description;
    @NotNull // или проверка?
    private User requester;
    private LocalDateTime created;
}
