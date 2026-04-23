package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemRequestCreate;
import ru.practicum.shareit.item.dto.ItemRequestUpdate;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();

        dto.setId(item.getId());
        dto.setAvailable(item.getAvailable());
        dto.setDescription(item.getDescription());
        dto.setName(item.getName());

        return dto;
    }

    public static ItemDto mapToItemDto(Item item, List<CommentDto> comments) {
        ItemDto dto = new ItemDto();

        dto.setId(item.getId());
        dto.setAvailable(item.getAvailable());
        dto.setDescription(item.getDescription());
        dto.setName(item.getName());
        dto.setComments(comments);

        return dto;
    }

    public static ItemDtoForOwner mapToItemDto(Item item,
                                               LocalDateTime lastBooking,
                                               LocalDateTime nextBooking,
                                               List<CommentDto> comments) {
        ItemDtoForOwner dto = new ItemDtoForOwner();

        dto.setId(item.getId());
        dto.setAvailable(item.getAvailable());
        dto.setDescription(item.getDescription());
        dto.setName(item.getName());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);

        return dto;
    }

    public static Item mapItemFromCreateReq(ItemRequestCreate itemRequestCreate) {
        Item item = new Item();
        item.setName(itemRequestCreate.getName());
        item.setAvailable(itemRequestCreate.getAvailable());
        item.setDescription(itemRequestCreate.getDescription());

        return item;
    }

    public static Item mapItemFromUpdateReq(ItemRequestUpdate itemRequestUpdate) {
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
