package ru.practicum.server.item.storage;

import ru.practicum.server.item.model.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.user.model.User;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerOrderById(User user);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
            String name, String description, Boolean available);

    List<Item> findAllByRequestIdIn(List<Long> requestsId);

    List<Item> findItemsByRequestId(long requestId);
}