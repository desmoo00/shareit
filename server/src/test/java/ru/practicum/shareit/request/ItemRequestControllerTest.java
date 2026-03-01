package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.HeaderConstants;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void addRequest_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Description", LocalDateTime.now(), List.of());

        when(itemRequestService.addRequest(eq(userId), any())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void getOwnRequests_ShouldReturnList() throws Exception {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Description", LocalDateTime.now(), List.of());

        when(itemRequestService.getOwnRequests(userId)).thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));
    }
}
