package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.util.HeaderConstants;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class BaseClientTest {
    private RestTemplate restTemplate;
    private TestClient client;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        client = new TestClient(restTemplate);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok("ok"));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), any(Map.class)))
                .thenReturn(ResponseEntity.ok("ok"));
    }

    @Test
    void shouldCallAllOverloads() {
        assertEquals(HttpStatus.OK, client.getNoUser("/a").getStatusCode());
        assertEquals(HttpStatus.OK, client.getWithUser("/a", 1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.getWithParams("/a", 1L, Map.of("x", 1)).getStatusCode());

        assertEquals(HttpStatus.OK, client.postNoUser("/b", "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.postWithUser("/b", 1L, "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.postWithParams("/b", 1L, Map.of("x", 1), "body").getStatusCode());

        assertEquals(HttpStatus.OK, client.putWithUser("/c", 1L, "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.putWithParams("/c", 1L, Map.of("x", 1), "body").getStatusCode());

        assertEquals(HttpStatus.OK, client.patchNoUser("/d", "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.patchOnlyUser("/d", 1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.patchWithUser("/d", 1L, "body").getStatusCode());
        assertEquals(HttpStatus.OK, client.patchWithParams("/d", 1L, Map.of("x", 1), "body").getStatusCode());

        assertEquals(HttpStatus.OK, client.deleteNoUser("/e").getStatusCode());
        assertEquals(HttpStatus.OK, client.deleteWithUser("/e", 1L).getStatusCode());
        assertEquals(HttpStatus.OK, client.deleteWithParams("/e", 1L, Map.of("x", 1)).getStatusCode());
    }

    @Test
    void shouldHandleHttpStatusCodeException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "bad",
                null,
                "error".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(ex);

        ResponseEntity<Object> response = client.getNoUser("/broken");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldPrepareNon2xxResponseWithAndWithoutBody() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad body"));

        ResponseEntity<Object> withBody = client.getNoUser("/bad");
        assertEquals(HttpStatus.BAD_REQUEST, withBody.getStatusCode());
        assertEquals("bad body", withBody.getBody());

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        ResponseEntity<Object> withoutBody = client.getNoUser("/empty");
        assertEquals(HttpStatus.NOT_FOUND, withoutBody.getStatusCode());
        assertNull(withoutBody.getBody());
    }

    @Test
    void shouldCoverHeaderConstants() {
        assertEquals("X-Sharer-User-Id", HeaderConstants.USER_ID_HEADER);
    }

    private static class TestClient extends BaseClient {
        TestClient(RestTemplate rest) {
            super(rest);
        }

        ResponseEntity<Object> getNoUser(String path) {
            return get(path);
        }

        ResponseEntity<Object> getWithUser(String path, long userId) {
            return get(path, userId);
        }

        ResponseEntity<Object> getWithParams(String path, Long userId, Map<String, Object> params) {
            return get(path, userId, params);
        }

        <T> ResponseEntity<Object> postNoUser(String path, T body) {
            return post(path, body);
        }

        <T> ResponseEntity<Object> postWithUser(String path, long userId, T body) {
            return post(path, userId, body);
        }

        <T> ResponseEntity<Object> postWithParams(String path, Long userId, Map<String, Object> params, T body) {
            return post(path, userId, params, body);
        }

        <T> ResponseEntity<Object> putWithUser(String path, long userId, T body) {
            return put(path, userId, body);
        }

        <T> ResponseEntity<Object> putWithParams(String path, long userId, Map<String, Object> params, T body) {
            return put(path, userId, params, body);
        }

        <T> ResponseEntity<Object> patchNoUser(String path, T body) {
            return patch(path, body);
        }

        ResponseEntity<Object> patchOnlyUser(String path, long userId) {
            return patch(path, userId);
        }

        <T> ResponseEntity<Object> patchWithUser(String path, long userId, T body) {
            return patch(path, userId, body);
        }

        <T> ResponseEntity<Object> patchWithParams(String path, Long userId, Map<String, Object> params, T body) {
            return patch(path, userId, params, body);
        }

        ResponseEntity<Object> deleteNoUser(String path) {
            return delete(path);
        }

        ResponseEntity<Object> deleteWithUser(String path, long userId) {
            return delete(path, userId);
        }

        ResponseEntity<Object> deleteWithParams(String path, Long userId, Map<String, Object> params) {
            return delete(path, userId, params);
        }
    }
}
