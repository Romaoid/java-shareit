package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemStorageTest {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private Long ownerId;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        ownerId = userStorage.save(owner).getId();
    }

    @Test
    void findItemsByOwner_shouldReturnItemsByOwner() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(ownerId);
        itemStorage.save(item);

        List<Item> result = itemStorage.findItemsByOwner(ownerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void existsByOwner_shouldReturnTrueWhenUserHasItems() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(ownerId);
        itemStorage.save(item);

        boolean exists = itemStorage.existsByOwner(ownerId);

        assertThat(exists).isTrue();
    }

    @Test
    void search_shouldReturnItemsMatchingText() {
        Item item1 = new Item();
        item1.setName("Drill Pro");
        item1.setDescription("Powerful drill");
        item1.setAvailable(true);
        item1.setOwner(ownerId);

        Item item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer");
        item2.setAvailable(true);
        item2.setOwner(ownerId);

        itemStorage.save(item1);
        itemStorage.save(item2);

        List<Item> result = itemStorage.search("drill");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Drill Pro");
    }
}
