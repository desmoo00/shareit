package ru.practicum.shareit.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EntityEqualityTest {

    @Test
    void userEqualsAndHashCode_ShouldUseId() {
        User a = new User();
        User b = new User();
        a.setId(1L);
        b.setId(1L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null);
    }

    @Test
    void itemEqualsAndHashCode_ShouldUseId() {
        Item a = new Item();
        Item b = new Item();
        a.setId(1L);
        b.setId(1L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null);
    }

    @Test
    void commentEqualsAndHashCode_ShouldUseId() {
        Comment a = new Comment();
        Comment b = new Comment();
        a.setId(1L);
        b.setId(1L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null);
    }

    @Test
    void bookingEqualsAndHashCode_ShouldUseId() {
        Booking a = new Booking();
        Booking b = new Booking();
        a.setId(1L);
        b.setId(1L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null);
    }
}
