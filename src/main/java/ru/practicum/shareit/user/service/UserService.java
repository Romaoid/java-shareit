package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestCreate;
import ru.practicum.shareit.user.dto.UserRequestUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
public class UserService {
    @Autowired
    private UserStorage userStorage;

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
        if (updateUser.getEmail() != null) {
            isMailInStorage(updateUser.getEmail());
        }

        return UserMapper.mapToUserDto(
                userStorage.updateUser(
                        UserMapper.mapUserFromUpdateReq(updateUser)));
    }

    public void deleteUser(Long id) {
        if (hasUserInStorage(id)) {
            userStorage.deleteUser(id);
        }
    }

    private boolean hasUserInStorage(Long id) {
        if (id == null) {
            throw new RuntimeException("null");
        }
        if (userStorage.getUserById(id) == null) {
            throw new RuntimeException("user not found");
        }

        return true;
    }

    private void isMailInStorage(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email error");
        }

        long usersWithEmail = userStorage.getUsers()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .count();
        if (usersWithEmail > 0) {
            throw new RuntimeException("email already exist");
        }
    }
}
