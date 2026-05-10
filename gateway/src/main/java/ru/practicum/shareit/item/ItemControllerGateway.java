package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HttpHeaders;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.dto.CommentDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemControllerGateway {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                                          @Valid @RequestBody ItemDtoCreate newItem) {
        return itemClient.addItem(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody ItemDtoUpdate updatedItem) {
        return itemClient.updateItem(userId, itemId, updatedItem);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId) {
        return itemClient.getItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto request) {
        return itemClient.addComment(userId, itemId, request);
    }
}