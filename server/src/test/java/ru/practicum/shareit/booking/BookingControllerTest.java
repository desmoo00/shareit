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
import java.util.List;

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

    @Test
    void getBooking_ShouldReturnBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 10L;
        BookingDto bookingDto = new BookingDto(bookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L, null, null, BookingStatus.WAITING);

        when(bookingService.getBooking(userId, bookingId)).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void getUserBookings_ShouldReturnList() throws Exception {
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto(11L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L, null, null, BookingStatus.WAITING);

        when(bookingService.getUserBookings(userId, "ALL", 0, 10)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11L));
    }

    @Test
    void getOwnerBookings_ShouldReturnList() throws Exception {
        Long userId = 2L;
        BookingDto bookingDto = new BookingDto(12L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), 2L, null, null, BookingStatus.APPROVED);

        when(bookingService.getOwnerBookings(userId, "APPROVED", 0, 5)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(HeaderConstants.USER_ID_HEADER, userId)
                        .param("state", "APPROVED")
                        .param("from", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(12L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }
}
