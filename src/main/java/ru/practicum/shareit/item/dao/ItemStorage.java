package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ItemStorage extends JpaRepository<Item, Long> {

    Item findItemById(long id);

    List<Item> findItemsByUserId(long userId);

    @Query("select item from Item as item " +
            "where item.available = true " +
            "and (lower(item.name) like concat('%', lower(?1), '%') " +
            "or lower(item.description) like concat('%', lower(?1), '%'))")
    List<Item> search(String keyWord);

    Item save(Item item);
}
