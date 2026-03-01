package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.util.HeaderConstants;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerGatewayTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void addBooking_ShouldReturnBadRequest_WhenStartIsInPast() throws Exception {
        Long userId = 1L;
        // Время начала в прошлом
        BookItemRequestDto requestDto = new BookItemRequestDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_ShouldReturnBadRequest_WhenEndIsBeforeStart() throws Exception {
        Long userId = 1L;
        // Время окончания раньше времени начала
        BookItemRequestDto requestDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
