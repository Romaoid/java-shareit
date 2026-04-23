package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemDtoForOwner {
    private long id;
    private String description;
    private String name;
    private boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
}