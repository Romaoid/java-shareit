package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForAnswerRequest;

import java.time.Instant;
import java.util.List;

@Data
public class AnswerRequestDto {
    private Long id;
    private String description;
    private Instant created;
    private List<ItemDtoForAnswerRequest> items;
}
