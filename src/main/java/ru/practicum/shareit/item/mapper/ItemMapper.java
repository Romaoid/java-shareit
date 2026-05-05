package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.*;
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

    public static ItemDtoForAnswerRequest mapToItemDtoForAnswerRequest(Item item) {
        ItemDtoForAnswerRequest dto = new ItemDtoForAnswerRequest();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner());

        return dto;
    }

    public static Item mapItemFromCreateReq(ItemRequestCreate itemRequestCreate) {
        Item item = new Item();
        item.setName(itemRequestCreate.getName());
        item.setAvailable(itemRequestCreate.getAvailable());
        item.setDescription(itemRequestCreate.getDescription());

        return item;
    }

    public static Item mapItemFromUpdateReq(Item item, ItemRequestUpdate itemRequestUpdate) {

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
