package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        // Невалидный email
        UserDto userDto = new UserDto(null, "John", "invalid-email");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        // Пустое имя
        UserDto userDto = new UserDto(null, "", "john@mail.com");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
