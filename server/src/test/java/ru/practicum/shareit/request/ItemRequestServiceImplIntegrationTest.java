package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void shouldCreateAndGetRequestWithItems() {
        UserDto requestor = userService.createUser(new UserDto(null, "Requestor", "req@mail.com"));
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));

        ItemRequestDto requestDto = new ItemRequestDto(null, "Need drill", null, null);
        ItemRequestDto savedRequest = itemRequestService.addRequest(requestor.getId(), requestDto);

        assertNotNull(savedRequest.getId());
        assertEquals("Need drill", savedRequest.getDescription());

        ItemDto itemDto = new ItemDto(null, "Drill", "Good one", true, savedRequest.getId());
        itemService.addItem(owner.getId(), itemDto);

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(requestor.getId());

        assertEquals(1, ownRequests.size());
        assertEquals(savedRequest.getId(), ownRequests.get(0).getId());
        assertNotNull(ownRequests.get(0).getItems());
        assertEquals(1, ownRequests.get(0).getItems().size());
        assertEquals("Drill", ownRequests.get(0).getItems().get(0).getName());
        assertEquals(owner.getId(), ownRequests.get(0).getItems().get(0).getOwnerId());
    }

    @Test
    void shouldReturnAllRequestsExceptOwn() {
        UserDto user1 = userService.createUser(new UserDto(null, "User1", "u1@mail.com"));
        UserDto user2 = userService.createUser(new UserDto(null, "User2", "u2@mail.com"));

        itemRequestService.addRequest(user1.getId(), new ItemRequestDto(null, "Request 1", null, null));
        ItemRequestDto req2 = itemRequestService.addRequest(user2.getId(), new ItemRequestDto(null, "Request 2", null, null));

        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests(user1.getId(), 0, 10);

        assertEquals(1, allRequests.size());
        assertEquals(req2.getId(), allRequests.get(0).getId());
        assertEquals("Request 2", allRequests.get(0).getDescription());
    }

    @Test
    void shouldGetRequestById() {
        UserDto requestor = userService.createUser(new UserDto(null, "Requestor2", "requestor2@mail.com"));
        UserDto viewer = userService.createUser(new UserDto(null, "Viewer", "viewer@mail.com"));

        ItemRequestDto created = itemRequestService.addRequest(
                requestor.getId(),
                new ItemRequestDto(null, "Need screwdriver", null, null)
        );

        ItemRequestDto byId = itemRequestService.getRequestById(viewer.getId(), created.getId());

        assertEquals(created.getId(), byId.getId());
        assertEquals("Need screwdriver", byId.getDescription());
    }
}
