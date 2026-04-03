package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserStorageTemporary implements UserStorage {
    private static long lastId = 0;
    private static final List<Long> freeIdList = new ArrayList<>();

    public User getUserById(Long id) {
        if (hasInStorage(id)) {
            return userStorage.get(id);
        }
        return null;
    }

    public User addUser(User user) {
        long id = getNewId();

        user.setId(id);
        userStorage.put(id, user);

        return user;
    }

    public User updateUser(User user) {
        User updatedUser = userStorage.get(user.getId());

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        return updatedUser;
    }

    public void deleteUser(Long id) {
        if (hasInStorage(id)) {
            freeIdList.add(id);
        }
        userStorage.remove(id);
    }

    public List<User> getUsers() {
        return userStorage.values().stream().toList();
    }

    private static long getNewId() {
        if (freeIdList.isEmpty()) {
            return ++lastId;
        }
        return freeIdList.removeFirst();
    }

    private static boolean hasInStorage(Long id) {
        return id != null && userStorage.containsKey(id);
    }
}
