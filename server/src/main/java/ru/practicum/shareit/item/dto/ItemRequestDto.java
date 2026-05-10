package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemRequestDto {
    private String description;
    private String name;
    private Boolean available;
    private Long requestId;

    public boolean isNameNotNull() {
        return name != null;
    }

    public boolean isDescriptionNotNull() {
        return description != null;
    }

    public boolean isAvailableNotNull() {
        return available != null;
    }
}
