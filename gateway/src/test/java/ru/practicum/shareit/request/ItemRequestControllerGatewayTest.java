package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.HeaderConstants;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerGatewayTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void addRequest_ShouldReturnBadRequest_WhenDescriptionBlank() throws Exception {
        ItemRequestDto request = new ItemRequestDto(null, "", null, null);

        mvc.perform(post("/requests")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addRequest_ShouldReturnOk_WhenValid() throws Exception {
        ItemRequestDto request = new ItemRequestDto(null, "Need drill", null, null);
        when(itemRequestClient.addRequest(eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        mvc.perform(post("/requests")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRequestClient).addRequest(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void getOwnRequests_ShouldReturnOk() throws Exception {
        when(itemRequestClient.getOwnRequests(1L)).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1L))));

        mvc.perform(get("/requests")
                        .header(HeaderConstants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemRequestClient).getOwnRequests(1L);
    }

    @Test
    void getAllRequests_ShouldReturnOk() throws Exception {
        when(itemRequestClient.getAllRequests(1L, 0, 10)).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 2L))));

        mvc.perform(get("/requests/all")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(1L, 0, 10);
    }

    @Test
    void getAllRequests_ShouldReturnBadRequest_WhenFromNegative() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRequests_ShouldReturnBadRequest_WhenSizeZero() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById_ShouldReturnOk() throws Exception {
        when(itemRequestClient.getRequestById(1L, 7L)).thenReturn(ResponseEntity.ok(Map.of("id", 7L)));

        mvc.perform(get("/requests/{requestId}", 7L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequestById(1L, 7L);
    }
}
