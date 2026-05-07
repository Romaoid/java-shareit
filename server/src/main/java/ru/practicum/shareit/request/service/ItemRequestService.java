package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.request.dao.RequestStorage;
import ru.practicum.shareit.request.dto.AnswerRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;

    @Transactional
    public ItemRequestDto addRequest(long authorId, ItemRequestDto request) {
        ItemRequest ir = new ItemRequest();
        ir.setAuthor(userStorage.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found")));
        ir.setDescription(request.getDescription());

        return RequestMapper.mapToDto(
                requestStorage.save(ir));
    }

    public List<AnswerRequestDto> getRequestsSelf(long userId) {
        validateUserById(userId);

        return requestStorage.findAllByAuthorId(userId)
                .stream()
                .map(RequestMapper::mapToDtoWithAnswers)
                .toList();
    }

    public List<ItemRequestDto> getRequestsOther(long userId) {
        validateUserById(userId);

        return requestStorage.findAllByAuthorIdIsNot(userId)
                .stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(RequestMapper::mapToDto)
                .toList();
    }

    public AnswerRequestDto getRequestById(long userId, long requestId) {
        validateUserById(userId);

        return RequestMapper.mapToDtoWithAnswers(
                requestStorage.findById(requestId)
                        .orElseThrow(() -> new NotFoundException("request not found"))
        );
    }

    private void validateUserById(Long id) {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("User not found");
        }
    }
}
