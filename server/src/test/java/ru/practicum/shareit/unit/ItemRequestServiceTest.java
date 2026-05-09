package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.request.dao.RequestStorage;
import ru.practicum.shareit.request.dto.AnswerRequestDto;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private RequestStorage requestStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private ItemRequestService requestService;

    @Test
    void addRequest_shouldCreateRequestSuccessfully() {
        long id = 1L;
        NewRequestDto newRequest = new NewRequestDto();
        newRequest.setDescription("Test description");

        User existingUser = new User();
        existingUser.setId(id);

        ItemRequest savedRequest = new ItemRequest();
        savedRequest.setAuthor(existingUser);
        savedRequest.setId(id);
        savedRequest.setDescription("Test description");

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(existingUser));
        when(requestStorage.save(any(ItemRequest.class)))
                .thenReturn(savedRequest);

        NewRequestDto result = requestService.addRequest(id, newRequest);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo(newRequest.getDescription());

        verify(requestStorage, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void addRequest_shouldThrowExceptionWhenAuthorNotFound() {
        NewRequestDto requestDto = new NewRequestDto();
        requestDto.setDescription("Need a drill");

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException e = Assertions.assertThrows(
                NotFoundException.class, () -> requestService.addRequest(1L, requestDto));

        assertThat(e.getMessage()).isEqualTo("User not found");

        verify(requestStorage, never()).save(any(ItemRequest.class));
    }

    @Test
    void getRequestsSelf_shouldReturnUsersRequestsWithAnswers() {
        long userId = 1L;

        User author = new User();
        author.setId(userId);

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Need a drill");
        request1.setAuthor(author);
        request1.setCreated(Instant.now().minusSeconds(3600));
        request1.setItems(Collections.emptySet());

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Need a hammer");
        request2.setAuthor(author);
        request2.setCreated(Instant.now());
        request2.setItems(Collections.emptySet());

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(requestStorage.findAllByAuthorId(userId))
                .thenReturn(List.of(request1, request2));

        List<AnswerRequestDto> result = requestService.getRequestsSelf(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Need a drill");
        assertThat(result.get(1).getDescription()).isEqualTo("Need a hammer");

        verify(requestStorage, times(1)).findAllByAuthorId(userId);
    }

    @Test
    void getRequestsSelf_shouldThrowExceptionWhenUserNotFound() {
        long userId = 999L;

        when(userStorage.existsById(userId))
                .thenReturn(false);

        assertThatThrownBy(() -> requestService.getRequestsSelf(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(requestStorage, never()).findAllByAuthorId(anyLong());
    }

    @Test
    void getRequestsOther_shouldReturnOtherUsersRequestsSorted() {
        long userId = 1L;

        User otherUser = new User();
        otherUser.setId(2L);

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Older request");
        request1.setCreated(Instant.now().minusSeconds(7200));

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Newer request");
        request2.setCreated(Instant.now().minusSeconds(3600));

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(requestStorage.findAllByAuthorIdIsNot(userId))
                .thenReturn(List.of(request1, request2));

        List<NewRequestDto> result = requestService.getRequestsOther(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Newer request");
        assertThat(result.get(1).getDescription()).isEqualTo("Older request");

        verify(requestStorage, times(1)).findAllByAuthorIdIsNot(userId);
    }

    @Test
    void getRequestById_shouldReturnRequestWithAnswers() {
        long userId = 1L;
        long requestId = 1L;

        User author = new User();
        author.setId(userId);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        request.setDescription("Need a drill");
        request.setAuthor(author);
        request.setCreated(Instant.now());
        request.setItems(Set.of());

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(requestStorage.findById(requestId))
                .thenReturn(Optional.of(request));

        AnswerRequestDto result = requestService.getRequestById(userId, requestId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getDescription()).isEqualTo("Need a drill");

        verify(requestStorage, times(1)).findById(requestId);
    }

    @Test
    void getRequestById_shouldThrowExceptionWhenRequestNotFound() {
        long userId = 1L;
        long requestId = 999L;

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(requestStorage.findById(requestId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getRequestById(userId, requestId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("request not found");
    }
}
