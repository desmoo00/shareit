package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerGatewayTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        UserDto userDto = new UserDto(null, "John", "invalid-email");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        UserDto userDto = new UserDto(null, "", "john@mail.com");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnOk_WhenBodyIsValid() throws Exception {
        UserDto request = new UserDto(null, "John", "john@mail.com");
        UserDto response = new UserDto(1L, "John", "john@mail.com");
        when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok(response));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userClient).create(any(UserDto.class));
    }

    @Test
    void getAllUsers_ShouldReturnOk() throws Exception {
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok(List.of(new UserDto(1L, "John", "john@mail.com"))));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getUser_ShouldReturnOk() throws Exception {
        when(userClient.getUser(1L)).thenReturn(ResponseEntity.ok(new UserDto(1L, "John", "john@mail.com")));

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userClient).getUser(1L);
    }

    @Test
    void updateUser_ShouldReturnOk() throws Exception {
        UserDto request = new UserDto(null, "Updated", "updated@mail.com");
        UserDto response = new UserDto(1L, "Updated", "updated@mail.com");
        when(userClient.update(eq(1L), any(UserDto.class))).thenReturn(ResponseEntity.ok(response));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(userClient).update(eq(1L), any(UserDto.class));
    }

    @Test
    void deleteUser_ShouldReturnOk() throws Exception {
        when(userClient.deleteUser(1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(1L);
    }
}
