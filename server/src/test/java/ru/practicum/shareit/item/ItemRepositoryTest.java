package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void search_ShouldReturnItems_WhenTextMatchesNameOrDescription() {
        // Подготовка данных
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        userRepository.save(owner);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Мощная дрель");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Аккумуляторная дрель-шуруповерт");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Пила");
        item3.setDescription("Обычная дрель");
        item3.setAvailable(false); // Недоступна (не должна попасть в выдачу)
        item3.setOwner(owner);
        itemRepository.save(item3);

        // Действие: Ищем по слову "дрель" (без учета регистра)
        List<Item> result = itemRepository.search("дРЕлЬ");

        // Проверка: Должны найтись item1 и item2, а item3 пропущен из-за available = false
        assertEquals(2, result.size());
    }
}
