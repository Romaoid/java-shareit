package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Instant created;
}
