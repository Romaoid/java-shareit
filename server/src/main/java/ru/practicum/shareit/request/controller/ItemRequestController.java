package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.constants.HttpHeaders;
import ru.practicum.shareit.request.dto.AnswerRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long authorId,
                                     @RequestBody ItemRequestDto request) {
        return requestService.addRequest(authorId, request);
    }

    @GetMapping
    public List<AnswerRequestDto> getRequestsSelf(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId) {
        return requestService.getRequestsSelf(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsOther(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId) {
        return requestService.getRequestsOther(userId);
    }

    @GetMapping("/{requestId}")
    public AnswerRequestDto getRequestById(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                                           @PathVariable("requestId") long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
