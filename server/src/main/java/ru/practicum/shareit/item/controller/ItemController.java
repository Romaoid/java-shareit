package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.controller.constants.HttpHeaders;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                           @RequestBody ItemRequestDto newItem) {
        return itemService.addItem(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemRequestDto updatedItem) {
        return itemService.updateItem(userId, itemId, updatedItem);
    }

    @GetMapping
    public List<ItemDtoForOwner> getItems(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId) {
        return itemService.getItemsByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoForOwner getItem(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.getItemsBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentRequestDto request) {
        return itemService.addComment(userId, itemId, request);
    }
}
