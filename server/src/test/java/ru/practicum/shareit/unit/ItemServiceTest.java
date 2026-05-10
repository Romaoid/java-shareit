package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.error.exception.ValidateException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.RequestStorage;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private RequestStorage requestStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;

    @InjectMocks
    private ItemService itemService;

    @Test
    void addItem_shouldCreateItemSuccessfullyWithoutRequestId() {
        long ownerId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Drill");
        requestDto.setDescription("Powerful drill");
        requestDto.setAvailable(true);
        requestDto.setRequestId(null);

        User owner = new User();
        owner.setId(ownerId);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Drill");
        savedItem.setDescription("Powerful drill");
        savedItem.setAvailable(true);
        savedItem.setOwner(ownerId);

        when(userStorage.existsById(ownerId))
                .thenReturn(true);
        when(itemStorage.save(any(Item.class)))
                .thenReturn(savedItem);

        ItemDto result = itemService.addItem(ownerId, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.isAvailable()).isTrue();

        verify(itemStorage, times(1)).save(any(Item.class));
        verify(requestStorage, never()).addItemToRequest(anyLong(), anyLong());
    }

    @Test
    void addItem_shouldCreateItemWithRequestIdAndAddToAnswersRequests() {
        long ownerId = 1L;
        long requestId = 10L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Drill");
        requestDto.setDescription("Powerful drill");
        requestDto.setAvailable(true);
        requestDto.setRequestId(requestId);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Drill");
        savedItem.setOwner(ownerId);
        savedItem.setAvailable(true);

        when(userStorage.existsById(ownerId))
                .thenReturn(true);
        when(requestStorage.existsById(requestId))
                .thenReturn(true);
        when(itemStorage.save(any(Item.class)))
                .thenReturn(savedItem);

        ItemDto result = itemService.addItem(ownerId, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.isAvailable()).isTrue();

        verify(requestStorage, times(1)).addItemToRequest(requestId, 1L);
    }

    @Test
    void addItem_shouldThrowExceptionWhenUserNotFound() {
        long ownerId = 999L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Drill");

        when(userStorage.existsById(ownerId))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.addItem(ownerId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("user not found");

        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void addItem_shouldThrowExceptionWhenRequestIdNotFound() {
        long ownerId = 1L;
        long requestId = 999L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Drill");
        requestDto.setRequestId(requestId);

        when(userStorage.existsById(ownerId))
                .thenReturn(true);
        when(requestStorage.existsById(requestId))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.addItem(ownerId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Request not found");

        verify(itemStorage, times(1)).save(any(Item.class));
        verify(requestStorage, never()).addItemToRequest(anyLong(), anyLong());
    }

    @Test
    void updateItem_shouldUpdateItemSuccessfully() {
        long ownerId = 1L;
        long itemId = 1L;
        ItemRequestDto updateRequest = new ItemRequestDto();
        updateRequest.setName("Updated Drill");
        updateRequest.setDescription("Updated description");
        updateRequest.setAvailable(false);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Drill");
        existingItem.setDescription("Old description");
        existingItem.setAvailable(true);
        existingItem.setOwner(ownerId);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("Updated Drill");
        updatedItem.setDescription("Updated description");
        updatedItem.setAvailable(false);
        updatedItem.setOwner(ownerId);

        when(userStorage.existsById(ownerId))
                .thenReturn(true);
        when(itemStorage.findById(itemId))
                .thenReturn(Optional.of(existingItem));
        when(itemStorage.save(any(Item.class)))
                .thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(ownerId, itemId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Drill");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.isAvailable()).isFalse();

        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowExceptionWhenUserIsNotOwner() {
        long ownerId = 1L;
        long otherUserId = 2L;
        long itemId = 1L;
        ItemRequestDto updateRequest = new ItemRequestDto();
        updateRequest.setName("Updated Drill");

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(ownerId);

        when(userStorage.existsById(otherUserId))
                .thenReturn(true);
        when(itemStorage.findById(itemId))
                .thenReturn(Optional.of(existingItem));

        assertThatThrownBy(() -> itemService.updateItem(otherUserId, itemId, updateRequest))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("shared by other user");

        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void getItemsBySearch_shouldReturnItemsMatchingText() {
        String searchText = "drill";

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Drill Pro");
        item1.setDescription("Powerful drill");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer");
        item2.setAvailable(true);

        when(itemStorage.search(searchText))
                .thenReturn(List.of(item1, item2));

        List<ItemDto> result = itemService.getItemsBySearch(searchText);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Drill Pro");
        assertThat(result.get(1).getName()).isEqualTo("Hammer");
    }

    @Test
    void getItemById_shouldReturnItemWhenExists() {
        long itemId = 1L;

        Item item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(1L);

        when(itemStorage.findById(itemId))
                .thenReturn(Optional.of(item));
        when(commentStorage.findByItemIdOrderByCreatedAsc(itemId))
                .thenReturn(Collections.emptyList());

        ItemDtoForOwner result = itemService.getItemById(itemId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Drill");
    }

    @Test
    void getItemById_shouldThrowExceptionWhenItemNotFound() {
        long itemId = 999L;

        when(itemStorage.findById(itemId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemById(itemId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("item not found");
    }

    @Test
    void addComment_shouldAddCommentSuccessfullyAfterCompletedBooking() {
        long userId = 1L;
        long itemId = 1L;
        CommentRequestDto request = new CommentRequestDto();
        request.setText("Great item!");

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");

        Item item = new Item();
        item.setId(itemId);
        item.setName("Drill");

        ru.practicum.shareit.booking.model.Booking booking = new ru.practicum.shareit.booking.model.Booking();
        booking.setId(1L);
        booking.setStatus(ru.practicum.shareit.booking.model.BookingStatus.APPROVED);
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setComment("Great item!");
        savedComment.setAuthor(user);
        savedComment.setItem(item);
        savedComment.setCreated(LocalDateTime.now());

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(itemStorage.existsById(itemId))
                .thenReturn(true);
        when(bookingStorage.findByBookerIdAndItemId(userId, itemId))
                .thenReturn(Optional.of(booking));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(savedComment);

        ru.practicum.shareit.item.comment.CommentDto result = itemService.addComment(userId, itemId, request);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Great item!");
        assertThat(result.getAuthorName()).isEqualTo("John Doe");

        verify(commentStorage, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowExceptionWhenUserDidNotBookItem() {
        long userId = 1L;
        long itemId = 1L;
        CommentRequestDto request = new CommentRequestDto();
        request.setText("Great item!");

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(itemStorage.existsById(itemId))
                .thenReturn(true);
        when(bookingStorage.findByBookerIdAndItemId(userId, itemId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(userId, itemId, request))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("doesn't book item");

        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowExceptionWhenBookingNotCompleted() {
        long userId = 1L;
        long itemId = 1L;
        CommentRequestDto request = new CommentRequestDto();
        request.setText("Great item!");

        Item item = new Item();
        item.setId(itemId);

        User user = new User();
        user.setId(userId);

        ru.practicum.shareit.booking.model.Booking booking = new ru.practicum.shareit.booking.model.Booking();
        booking.setId(1L);
        booking.setStatus(ru.practicum.shareit.booking.model.BookingStatus.APPROVED);
        booking.setEndDate(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(itemStorage.existsById(itemId))
                .thenReturn(true);
        when(bookingStorage.findByBookerIdAndItemId(userId, itemId))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> itemService.addComment(userId, itemId, request))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("You can leave a comment after completing your booking");

        verify(commentStorage, never()).save(any(Comment.class));
    }
}
