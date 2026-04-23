package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidateException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestCreate;
import ru.practicum.shareit.user.dto.UserRequestUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto getUserById(Long id) {
        if (hasUserInStorage(id)) {
            return UserMapper.mapToUserDto(userStorage.findUserById(id));
        }
        return null;
    }

    @Transactional
    public UserDto addUser(UserRequestCreate newUser) {
        isMailInStorage(newUser.getEmail());

        return UserMapper.mapToUserDto(
                userStorage.save(
                        UserMapper.mapUserFromCreateReq(newUser)));
    }

    @Transactional
    public UserDto updateUser(Long id, UserRequestUpdate updateUser) {
        User updatedUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found"));

        if (updateUser.getEmail() != null && !updateUser.getEmail().equals(updatedUser.getEmail())) {
            isMailInStorage(updateUser.getEmail());
        }

        if (updateUser.isNameNotNull()) updatedUser.setName(updateUser.getName());
        if (updateUser.isEmailNotNull()) updatedUser.setEmail(updateUser.getEmail());

        return UserMapper.mapToUserDto(
                userStorage.save(updatedUser));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (hasUserInStorage(id)) {
            userStorage.deleteById(id);
        }
    }

    private boolean hasUserInStorage(Long id) {
        if (id == null) {
            throw new ValidateException("id isn't correct");
        }
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("user not found");
        }

        return true;
    }

    private void isMailInStorage(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidateException("Email isn't correct");
        }

        if (userStorage.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exist!");
        }
    }
}
