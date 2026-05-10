package ru.practicum.shareit.unit;


import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    @Test
    void addUser_shouldCreateUserSuccessfully() {
        UserRequestDto request = new UserRequestDto();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");

        when(userStorage.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());
        when(userStorage.save(any(User.class)))
                .thenReturn(savedUser);

        UserDto result = userService.addUser(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");

        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void addUser_shouldThrowExceptionWhenEmailExists() {
        UserRequestDto request = new UserRequestDto();
        request.setEmail("existing@example.com");

        User existingUser = new User();

        when(userStorage.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.addUser(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exist!");

        verify(userStorage, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {
        long userId = 1L;
        UserRequestDto updateRequest = new UserRequestDto();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(existingUser));
        when(userStorage.findByEmail(updateRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(userStorage.save(any(User.class)))
                .thenReturn(updatedUser);

        UserDto result = userService.updateUser(userId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");

        verify(userStorage, times(1)).findById(anyLong());
        verify(userStorage, times(1)).findByEmail(anyString());
        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowExceptionWhenUserNotFound() {
        UserRequestDto updateRequest = new UserRequestDto();

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("user not found");

        verify(userStorage, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateOnlyNameWhenEmailIsNull() {
        long userId = 1L;
        UserRequestDto updateRequest = new UserRequestDto();
        updateRequest.setName("New Name");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class)))
                .thenReturn(existingUser);

        UserDto result = userService.updateUser(userId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("old@example.com");

        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("user not found");
    }
}
