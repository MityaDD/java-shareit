package ru.practicum.server.item;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.server.item.dto.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<Comment> jsonComment;
    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;


    @Test
    @DisplayName("ToComment")
    void testComment() throws Exception {
        LocalDateTime created = LocalDateTime.now().plusDays(1);
        Comment comment = new Comment(1L, "Комментарий к букингу", new Item(), "Вася", created);
        CommentDto commentDto = ItemMapper.toCommentDto(comment, new User());

        CommentDto commentDtoNew = new CommentDto(2L, "Комментарий к букингу из ДТО", new Item(), "Petr", created);
        Comment newComment = ItemMapper.toComment(commentDtoNew, new User(), new Item());

        JsonContent<Comment> result = jsonComment.write(newComment);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий к букингу из ДТО");
        assertThat(result).extractingJsonPathValue("$.created").isNotNull();

        JsonContent<CommentDto> resultDto = jsonCommentDto.write(commentDto);

        assertThat(resultDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultDto).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий к букингу");
        assertThat(resultDto).extractingJsonPathValue("$.created").isNotNull();
    }

    @Test
    @DisplayName("ToCommentDto")
    void testToCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().plusDays(1);
        User user = new User(1L, "Vladimir", "vova-boss@yandex.ru");
        Item item = new Item(2L, "Веник", "Домашний", user, true, 3L);
        Comment comment = new Comment(1L, "Комментарий к букингу", item, "Vladimir", created);

        CommentDto newComment = ItemMapper.toCommentDto(comment, user);

        JsonContent<CommentDto> result = jsonCommentDto.write(newComment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий к букингу");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Vladimir");
        assertThat(result).extractingJsonPathValue("$.created").isNotNull();
    }

}