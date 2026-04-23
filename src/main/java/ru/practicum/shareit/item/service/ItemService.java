package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidateException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemRequestCreate;
import ru.practicum.shareit.item.dto.ItemRequestUpdate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemService {
    @Autowired
    private ItemStorage itemStorage;
    private UserStorage userStorage;
    private BookingStorage bookingStorage;
    private CommentStorage commentStorage;

//    @Autowired
//    public ItemService(ItemStorage itemStorage,
//                       UserStorage userStorage) {
//        this.itemStorage = itemStorage;
//        this.userStorage = userStorage;
//    }

    @Transactional
    public ItemDto addItem(Long ownerId, ItemRequestCreate newItem) {
        validateUser(ownerId);

        Item addedItem = ItemMapper.mapItemFromCreateReq(newItem);
        addedItem.setOwner(ownerId);

        addedItem = itemStorage.save(addedItem);

        return  ItemMapper.mapToItemDto(addedItem);
    }

    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemRequestUpdate itemRequestUpdate) {
        validateUpdateRequest(ownerId,itemId);

        Item updatedItem = ItemMapper.mapItemFromUpdateReq(itemRequestUpdate);
        updatedItem.setOwner(ownerId);
        updatedItem.setId(itemId);

        updatedItem = itemStorage.save(updatedItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }

    public List<ItemDtoForOwner> getItemsByOwnerId(Long ownerId) {
        validateUser(ownerId);

        Map<Long, LocalDateTime> lastBookings = bookingStorage
                .findLastBookingDatesByOwnerId(ownerId)
                .stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (LocalDateTime) arr[1]
                ));

        Map<Long, LocalDateTime> nextBookings = bookingStorage
                .findNextBookingDatesByOwnerId(ownerId)
                .stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (LocalDateTime) arr[1]
                ));

        return itemStorage.findItemsByUserId(ownerId)
                .stream()
                .map(item -> ItemMapper.mapToItemDto(
                        item,
                        lastBookings.get(item.getId()),
                        nextBookings.get(item.getId()),
                        commentStorage.findByItemIdOrderByCreationDateAsc(item.getId())
                                .stream()
                                .map(CommentMapper::mapToCommentDto)
                                .toList()
                ))
                .toList();
    }

    public ItemDto getItemById(Long itemId) {
        List<CommentDto> comments = commentStorage.findByItemIdOrderByCreationDateAsc(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();

        return ItemMapper.mapToItemDto(
                itemStorage.findItemById(itemId),
                comments
                );
    }

    public List<ItemDto> getItemsBySearch(String text) {
        return itemStorage.search(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public CommentDto addComment(Long userId, Long itemId, CommentRequestDto request) {
        validateUser(userId);
        validateItemExisting(itemId);

        Booking booking = bookingStorage
                .findByOwnerIdAndItemId(userId, itemId)
                .orElseThrow(() ->
                        new ValidateException("user with id: " + userId + " doesn't book item with id: " + itemId));

        if (booking.getStatus() != BookingStatus.APPROVED
                || booking.getEndDate().toLocalDate().isAfter(LocalDate.now())) {
            String message;

            if (booking.getStatus() != BookingStatus.APPROVED) {
                message = String.format("Status of booking %d doesn't approve", booking.getId());
            } else {
                message = "You can leave a comment after completing your booking";
            }

            throw new ValidateException(message);
        }

        Comment comment = new Comment();
        comment.setComment(request.getText());
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());

        return CommentMapper.mapToCommentDto(
                commentStorage.save(comment)
        );
    }

    private void validateUser(Long id) {
        if (id == null) {
            throw new ValidateException("id isn't correct");
        }
        if (userStorage.findUserById(id).getId() == 0) {
            throw new NotFoundException("user not found");
        }
    }

    private void validateItemExisting(Long id) {
        if (id == null) {
            throw new ValidateException("id isn't correct");
        }
        if (!itemStorage.existsById(id)) {
            throw new NotFoundException("item not found");
        }
    }

    private void validateUpdateRequest(Long ownerId, Long itemId) {
        validateUser(ownerId);

        if (itemId == null) {
            throw new ValidateException("id isn't correct");
        }
        if (itemStorage.findItemById(itemId).getId() == 0) {
            throw new NotFoundException("item not found");
        }
        if (!Objects.equals(itemStorage.findItemById(itemId).getOwner(), ownerId)) {
            throw new ValidateException("Email is already exist");
        }
    }
}
