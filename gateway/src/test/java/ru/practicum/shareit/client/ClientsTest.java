package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ClientsTest {

    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        builder = Mockito.mock(RestTemplateBuilder.class);

        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(Mockito.<Supplier<ClientHttpRequestFactory>>any())).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), any(Map.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    }

    @Test
    void testUserClient() {
        UserClient client = new UserClient("http://localhost", builder);
        assertEquals(HttpStatus.OK, client.getAllUsers().getStatusCode());
        assertEquals(HttpStatus.OK, client.getUser(1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.create(new UserDto(1L, "n", "e")).getStatusCode());
        assertEquals(HttpStatus.OK, client.update(1L, new UserDto(1L, "n", "e")).getStatusCode());
        assertEquals(HttpStatus.OK, client.deleteUser(1L).getStatusCode());
    }

    @Test
    void testItemClient() {
        ItemClient client = new ItemClient("http://localhost", builder);
        assertEquals(HttpStatus.OK, client.addItem(1L, new ItemDto(1L, "n", "d", true, null)).getStatusCode());
        assertEquals(HttpStatus.OK, client.updateItem(1L, 1L, new ItemDto(1L, "n", "d", true, null)).getStatusCode());
        assertEquals(HttpStatus.OK, client.getUserItems(1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.searchItems("text").getStatusCode());
        assertEquals(HttpStatus.OK, client.getItem(1L, 1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.getItemWithoutUser(1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.addComment(1L, 1L, new CommentDto(1L, "t", "a", LocalDateTime.now())).getStatusCode());
    }

    @Test
    void testBookingClient() {
        BookingClient client = new BookingClient("http://localhost", builder);
        assertEquals(HttpStatus.OK, client.getBookings(1L, BookingState.ALL, 0, 10).getStatusCode());
        assertEquals(HttpStatus.OK, client.getOwnerBookings(1L, BookingState.ALL, 0, 10).getStatusCode());
        assertEquals(HttpStatus.OK, client.bookItem(1L, new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))).getStatusCode());
        assertEquals(HttpStatus.OK, client.approveBooking(1L, 1L, true).getStatusCode());
        assertEquals(HttpStatus.OK, client.getBooking(1L, 1L).getStatusCode());
    }

    @Test
    void testItemRequestClient() {
        ItemRequestClient client = new ItemRequestClient("http://localhost", builder);
        assertEquals(HttpStatus.OK, client.addRequest(1L, new ItemRequestDto(1L, "d", LocalDateTime.now(), null)).getStatusCode());
        assertEquals(HttpStatus.OK, client.getOwnRequests(1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.getAllRequests(1L, 0, 10).getStatusCode());
        assertEquals(HttpStatus.OK, client.getRequestById(1L, 1L).getStatusCode());
    }
}
