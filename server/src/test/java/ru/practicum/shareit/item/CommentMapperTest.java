package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentMapperTest {

    @Test
    void toComment_ShouldMapFields() {
        CommentDto dto = new CommentDto(null, "text", null, null);
        Item item = new Item();
        User author = new User();
        author.setName("author");

        Comment comment = CommentMapper.toComment(dto, item, author);

        assertEquals("text", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertNotNull(comment.getCreated());
    }

    @Test
    void toCommentDto_ShouldMapFields() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCreated(LocalDateTime.now());
        User author = new User();
        author.setName("author");
        comment.setAuthor(author);

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertEquals(1L, dto.getId());
        assertEquals("text", dto.getText());
        assertEquals("author", dto.getAuthorName());
        assertNotNull(dto.getCreated());
    }
}
