package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidateException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestCreate;
import ru.practicum.shareit.user.dto.UserRequestUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserStorTemp") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto getUserById(Long id) {
        if (hasUserInStorage(id)) {
            return UserMapper.mapToUserDto(userStorage.getUserById(id));
        }
        return null;
    }

    public UserDto addUser(UserRequestCreate newUser) {
        isMailInStorage(newUser.getEmail());

        return UserMapper.mapToUserDto(
                userStorage.addUser(
                        UserMapper.mapUserFromCreateReq(newUser)));
    }

    public UserDto updateUser(Long id, UserRequestUpdate updateUser) {
        hasUserInStorage(id);
        if (updateUser.getEmail() != null && !updateUser.getEmail().equals(userStorage.getUserById(id).getEmail())) {
            isMailInStorage(updateUser.getEmail());
        }

        User user = UserMapper.mapUserFromUpdateReq(updateUser);
        user.setId(id);

        return UserMapper.mapToUserDto(
                userStorage.updateUser(user));
    }

    public void deleteUser(Long id) {
        if (hasUserInStorage(id)) {
            userStorage.deleteUser(id);
        }
    }

    private boolean hasUserInStorage(Long id) {
        if (id == null) {
            throw new ValidateException("id isn't correct");
        }
        if (userStorage.getUserById(id).getId() == 0) {
            throw new NotFoundException("user not found");
        }

        return true;
    }

    private void isMailInStorage(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidateException("Email isn't correct");
        }

        long usersWithEmail = userStorage.getUsers()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .count();
        if (usersWithEmail > 0) {
            throw new RuntimeException("Email already exist!");
        }
    }
}
