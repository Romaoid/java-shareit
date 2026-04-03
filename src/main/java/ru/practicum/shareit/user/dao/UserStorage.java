package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserStorage {
    Map<Long, User> userStorage = new HashMap<>();

    User getUserById(Long id);

    List<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

}
