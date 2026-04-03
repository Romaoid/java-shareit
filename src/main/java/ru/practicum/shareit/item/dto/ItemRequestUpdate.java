package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemRequestUpdate {
   // private long id;
    private String description;
    private String name;
    private Boolean available;

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
