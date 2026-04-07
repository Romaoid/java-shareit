package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidateException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestCreate;
import ru.practicum.shareit.item.dto.ItemRequestUpdate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.List;

@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemService(@Qualifier("ItemStorTemp") ItemStorage itemStorage,
                       @Qualifier("UserStorTemp") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }


    public ItemDto addItem(Long ownerId, ItemRequestCreate newItem) {
        validateOwner(ownerId);

        Item addedItem = ItemMapper.mapItemFromCreateReq(newItem);
        addedItem.setOwner(ownerId);

        addedItem = itemStorage.addItem(addedItem);

        return  ItemMapper.mapToItemDto(addedItem);
    }

    public ItemDto updateItem(Long ownerId, Long itemId, ItemRequestUpdate itemRequestUpdate) {
        validateUpdateRequest(ownerId,itemId);

        Item updatedItem = ItemMapper.mapItemFromUpdateReq(itemRequestUpdate);
        updatedItem.setOwner(ownerId);
        updatedItem.setId(itemId);

        updatedItem = itemStorage.updateItem(updatedItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }

    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        validateOwner(ownerId);

        return itemStorage.getItemsByOwnerId(ownerId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto getItemById(Long itemId) {
        return ItemMapper.mapToItemDto(
                itemStorage.getItemById(itemId));
    }

    public List<ItemDto> getItemsBySearch(String text) {
        return itemStorage.getItemsBySearch(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    private void validateOwner(Long id) {
        if (id == null) {
            throw new ValidateException("id isn't correct");
        }
        if (userStorage.getUserById(id).getId() == 0) {
            throw new NotFoundException("user not found");
        }
    }

    private void validateUpdateRequest(Long ownerId, Long itemId) {
        validateOwner(ownerId);

        if (itemStorage.getItemById(itemId).getId() == 0) {
            throw new ValidateException("id isn't correct");
        }
        if (itemStorage.getItemById(itemId).getOwner() != ownerId) {
            throw new ValidateException("Email is already exist");
        }
    }
}
