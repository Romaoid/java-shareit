package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.AnswerRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ItemRequestDto mapToDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();

        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        return dto;
    }

    public static AnswerRequestDto mapToDtoWithAnswers(ItemRequest request) {
        AnswerRequestDto dto = new AnswerRequestDto();

        dto.setCreated(request.getCreated());
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        if (!request.getItems().isEmpty()) {
            dto.setItems(request.getItems()
                    .stream()
                    .map(ItemMapper::mapToItemDtoForAnswerRequest)
                    .toList());
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }
}
