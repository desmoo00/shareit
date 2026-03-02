package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.HeaderConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerGatewayTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void addItem_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        ItemDto bad = new ItemDto(null, "", "desc", true, null);

        mvc.perform(post("/items")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(bad))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_ShouldReturnOk_WhenValid() throws Exception {
        ItemDto request = new ItemDto(null, "Drill", "desc", true, null);
        when(itemClient.addItem(eq(1L), any(ItemDto.class))).thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        mvc.perform(post("/items")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).addItem(eq(1L), any(ItemDto.class));
    }

    @Test
    void updateItem_ShouldReturnOk() throws Exception {
        ItemDto request = new ItemDto(null, "Drill", "updated", true, null);
        when(itemClient.updateItem(eq(1L), eq(2L), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 2L)));

        mvc.perform(patch("/items/{itemId}", 2L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).updateItem(eq(1L), eq(2L), any(ItemDto.class));
    }

    @Test
    void getUserItems_ShouldReturnOk() throws Exception {
        when(itemClient.getUserItems(1L)).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1L))));

        mvc.perform(get("/items")
                        .header(HeaderConstants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemClient).getUserItems(1L);
    }

    @Test
    void search_ShouldReturnOk() throws Exception {
        when(itemClient.searchItems("drill")).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 3L))));

        mvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());

        verify(itemClient).searchItems("drill");
    }

    @Test
    void getItem_ShouldCallClientWithUser_WhenHeaderPresent() throws Exception {
        when(itemClient.getItem(1L, 2L)).thenReturn(ResponseEntity.ok(Map.of("id", 2L)));

        mvc.perform(get("/items/{itemId}", 2L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemClient).getItem(1L, 2L);
    }

    @Test
    void getItem_ShouldCallClientWithoutUser_WhenHeaderMissing() throws Exception {
        when(itemClient.getItemWithoutUser(2L)).thenReturn(ResponseEntity.ok(Map.of("id", 2L)));

        mvc.perform(get("/items/{itemId}", 2L))
                .andExpect(status().isOk());

        verify(itemClient).getItemWithoutUser(2L);
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenTextIsBlank() throws Exception {
        CommentDto bad = new CommentDto(null, "", null, null);

        mvc.perform(post("/items/{itemId}/comment", 3L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(bad))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_ShouldReturnOk_WhenValid() throws Exception {
        CommentDto request = new CommentDto(null, "good", null, LocalDateTime.now());
        when(itemClient.addComment(eq(1L), eq(3L), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 9L)));

        mvc.perform(post("/items/{itemId}/comment", 3L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).addComment(eq(1L), eq(3L), any(CommentDto.class));
    }
}
