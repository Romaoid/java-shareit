package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDtoUpdate {
    private String description;
    private String name;
    private Boolean available;
}
