package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDtoCreate {
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Available field is required")
    private Boolean available;

    private Long requestId;
}
