package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwner(long userId);

    Boolean existsByOwner(long userId);

    @Query("select item from Item as item " +
            "where item.available = true " +
            "and (lower(item.name) like concat('%', lower(?1), '%') " +
            "or lower(item.description) like concat('%', lower(?1), '%'))")
    List<Item> search(String keyWord);
}
