package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HttpHeaders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestControllerGateway {
    private final ItemRequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getRequestsSelf(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId) {
        return requestClient.getRequestsSelf(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsOther(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId) {
        return requestClient.getRequestsOther(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                                                 @PathVariable("requestId") long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long authorId,
                                             @Valid @RequestBody ItemRequestDto request) {
        return requestClient.addRequest(authorId, request);
    }
}
