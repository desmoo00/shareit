package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.HeaderConstants;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void addItem_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "Отвертка", "Крестовая отвертка", true, null);

        when(itemService.addItem(eq(userId), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void updateItem_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "Отвертка", "Обновленное описание", true, null);

        when(itemService.updateItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "Отвертка", "Крестовая", true, null);

        when(itemService.getItemById(userId, itemId)).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId));
    }

    @Test
    void getUserItems_ShouldReturnList() throws Exception {
        Long userId = 1L;
        when(itemService.getUserItems(userId)).thenReturn(List.of(new ItemDto(1L, "Item", "Desc", true, null)));

        mvc.perform(get("/items")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void searchItems_ShouldReturnList() throws Exception {
        String text = "Дрель";
        when(itemService.searchItems(text)).thenReturn(List.of(new ItemDto(2L, "Дрель", "Мощная", true, null)));

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void addComment_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto(1L, "Отличная вещь!", "User", LocalDateTime.now());

        when(itemService.addComment(eq(userId), eq(itemId), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }
}
