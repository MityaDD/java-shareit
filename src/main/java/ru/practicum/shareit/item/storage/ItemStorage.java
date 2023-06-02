package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerOrderById(User user);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
            String name, String description, Boolean available);

    // @Query(value = "select i from Item i " +
    //         "where lower(i.name) like %?1% or lower(i.description) like %?1% " +
    //        "and i.available=true")
    // List<Item> findByNameOrDescriptionLike(String text);
    //
    // List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
    //            String name, String description, Boolean available);
    //
    //  @Query("select i from Item as i " +
    //           "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
    //          "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
    //           "and i.available = true")
    //    Collection<Item> searchAvailableItems2(String text);
    // Collection<Item> findItemsByOwnerId(long ownerId);
}