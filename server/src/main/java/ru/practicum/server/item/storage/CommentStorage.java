package ru.practicum.server.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.item.model.Comment;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(long itemId);

    List<Comment> findAllByAndAuthorName(String author);
}