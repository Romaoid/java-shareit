package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
public class ItemDto {
    private long id;
    private String description;
    private String name;
    private boolean available;
    private List<CommentDto> comments;
}
