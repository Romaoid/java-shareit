package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ItemStorage {
    Map<Long, Item> itemStorage = new HashMap<>();

    Item getItemById(long id);

    List<Item> getItemsByOwnerId(long userId);

    List<Item> getItemsBySearch(String keyWord);

    Item addItem(Item newItem);

    Item updateItem(Item updatedItem);

}
