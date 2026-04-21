package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@Component("ItemStorTemp")
public class ItemStorageTemporary implements ItemStorage {
    private static long lastId = 0;
    private static final List<Long> freeIdList = new ArrayList<>();

    public Item getItemById(long id) {
        log.info("Поступил запрос на получение данных о предмете с id: {}", id);

        if (itemStorage.containsKey(id)) {
            return itemStorage.get(id);
        }
        log.info("Предмет с id: {} не найден", id);
        return new Item();
    }

    public List<Item> getItemsByOwnerId(long userId) {
        log.info("Поступил запрос на получение данных о предметах пользователя с id: {}", userId);
        return itemStorage.values()
                .stream()
                .filter(item -> item.getOwner() == userId)
                .toList();
    }

    public List<Item> getItemsBySearch(String keyWord) {
        log.info("Поступил запрос на поиск предметов по ключевому слову: {}", keyWord);
        if (keyWord == null || keyWord.isBlank()) {
            log.info("Алгоритм ничего не нашел");
            return new ArrayList<>();
        }

        return itemStorage.values()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(keyWord.toLowerCase())
                        || item.getDescription().toLowerCase().contains(keyWord.toLowerCase()))
                        && item.getAvailable() == true)
                .toList();
    }

    public Item addItem(Item newItem) {
        log.info("Поступил запрос на добавление предмета: name: {} с описанием: {} и доступностью: {} " +
                "пользователем с id: {}",
                newItem.getName(), newItem.getDescription(), newItem.getAvailable(), newItem.getOwner());
        long id = getNewId();

        newItem.setId(id);
        itemStorage.put(id, newItem);
        log.info("Предмет добавлен в хранилище под id: {}", id);

        return itemStorage.get(id);
    }

    public Item updateItem(Item updatedItem) {
        log.info("Поступил запрос на обновление данных о предмете под id: {}, c данными: name: {}, Description: {} "
                        + "и доступом:{}",
                updatedItem.getId(), updatedItem.getName(), updatedItem.getDescription(), updatedItem.getAvailable());
        Item item = itemStorage.get(updatedItem.getId());

        if (updatedItem.getDescription() != null) {
            item.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getName() != null) {
            item.setName(updatedItem.getName());
        }
        if (updatedItem.getAvailable() != null) {
            item.setAvailable(updatedItem.getAvailable());
        }

        return itemStorage.get(updatedItem.getId());
    }

    private static long getNewId() {
        if (freeIdList.isEmpty()) {
            return ++lastId;
        }
        return freeIdList.removeFirst();
    }
}
