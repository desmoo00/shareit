package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
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
        BookItemRequestDto requestDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_ShouldReturnOk_WhenBodyIsValid() throws Exception {
        long userId = 1L;
        BookItemRequestDto requestDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(bookingClient.bookItem(eq(userId), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 100L)));

        mvc.perform(post("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).bookItem(eq(userId), any(BookItemRequestDto.class));
    }

    @Test
    void getBookings_ShouldReturnOk() throws Exception {
        long userId = 2L;
        when(bookingClient.getBookings(userId, BookingState.ALL, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1L))));

        mvc.perform(get("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(userId, BookingState.ALL, 0, 10);
    }

    @Test
    void getBookings_ShouldReturnBadRequest_WhenStateIsUnknown() throws Exception {
        mvc.perform(get("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("state", "unknown")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnerBookings_ShouldReturnOk() throws Exception {
        long userId = 3L;
        when(bookingClient.getOwnerBookings(userId, BookingState.WAITING, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 2L))));

        mvc.perform(get("/bookings/owner")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .param("state", "waiting")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).getOwnerBookings(userId, BookingState.WAITING, 0, 10);
    }

    @Test
    void approveBooking_ShouldReturnOk() throws Exception {
        long userId = 4L;
        long bookingId = 10L;
        when(bookingClient.approveBooking(userId, bookingId, true))
                .thenReturn(ResponseEntity.ok(Map.of("status", "APPROVED")));

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(userId, bookingId, true);
    }

    @Test
    void getBooking_ShouldReturnOk() throws Exception {
        long userId = 5L;
        long bookingId = 12L;
        when(bookingClient.getBooking(userId, bookingId))
                .thenReturn(ResponseEntity.ok(Map.of("id", bookingId)));

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(userId, bookingId);
    }
}
