package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterOrderByCreatedDesc(Long userId);

    List<ItemRequest> findAllByRequesterIsNotOrderByCreatedDesc(Long requester, Pageable page);
}
