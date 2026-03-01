package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.util.HeaderConstants;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    void addBooking_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L, null, null, BookingStatus.WAITING);

        when(bookingService.addBooking(eq(userId), any(BookingDto.class))).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()));
    }

    @Test
    void approveBooking_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(bookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L, null, null, BookingStatus.APPROVED);

        when(bookingService.approveBooking(userId, bookingId, true)).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.name()));
    }
}
