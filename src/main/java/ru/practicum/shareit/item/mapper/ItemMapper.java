package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestCreate;
import ru.practicum.shareit.item.dto.ItemRequestUpdate;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();

        dto.setId(item.getId());
        dto.setAvailable(item.isAvailable());
        dto.setDescription(item.getDescription());
        dto.setName(item.getName());

        return dto;
    }

    public Item mapItemFromCreateReq(ItemRequestCreate itemRequestCreate) {
        Item item = new Item();
        item.setName(itemRequestCreate.getName());
        item.setAvailable(itemRequestCreate.getAvailable());
        item.setDescription(itemRequestCreate.getDescription());

        return item;
    }

    public Item mapItemFromUpdateReq(ItemRequestUpdate itemRequestUpdate) {
        Item item = new Item();

        if (itemRequestUpdate.isNameNotNull()) {
            item.setName(itemRequestUpdate.getName());
        }
        if (itemRequestUpdate.isDescriptionNotNull()) {
            item.setDescription(itemRequestUpdate.getDescription());
        }
        if (itemRequestUpdate.isAvailableNotNull()) {
            item.setAvailable(itemRequestUpdate.getAvailable());
        }

        return item;
    }
}
