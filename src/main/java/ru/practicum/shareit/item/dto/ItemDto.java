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
//    @NotBlank(message = "Description is required")
//    private String description;
//    @NotBlank(message = "Name is required")
//    private String name;
//    @NotNull(message = "Available field is required")
//    private Boolean available;
}
