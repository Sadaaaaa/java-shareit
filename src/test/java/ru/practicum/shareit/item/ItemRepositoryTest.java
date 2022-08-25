package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {


    private ItemRepository itemRepository;

    @Autowired
    public ItemRepositoryTest(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    @Test
    void get_allItemsTest() {
        Iterable<Item> items = itemRepository.findAll();
        assertThat(items).hasSize(0);
    }

    @Test
    void search_getItemFromCustomQuery() {
        Item item = Item.builder()
                .id(1)
                .name("New item")
                .description("Item for doing smth")
                .available(true)
                .build();

        itemRepository.save(item);
        List<Item> itemsFromRepo = itemRepository.search("ite", PageRequest.of(0, 5));
        assertThat(itemsFromRepo).hasSize(1);
        assertEquals(itemsFromRepo.get(0).getId(), 1);
        assertEquals(itemsFromRepo.get(0).getName(), "New item");
        assertEquals(itemsFromRepo.get(0).getDescription(), "Item for doing smth");
        assertEquals(itemsFromRepo.get(0).getAvailable(), true);
    }

}
