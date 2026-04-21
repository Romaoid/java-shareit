package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@Component("UserStorTemp")
public class UserStorageTemporary implements UserStorage {
    private static long lastId = 0;
    private static final List<Long> freeIdList = new ArrayList<>();

    public User getUserById(Long id) {
        log.info("Поступил запрос на получение данных о пользователе {}", id);

        if (hasInStorage(id)) {
            return userStorage.get(id);
        }
        log.info("Данных о пользователе {} нет, вернется \"пустой\" пользователь", id);
        return new User();
    }

    public User addUser(User user) {
        log.info("Поступил запрос на добавление пользователя c данными: name: {} и email: {}",
                user.getName(), user.getEmail());
        long id = getNewId();

        user.setId(id);
        userStorage.put(id, user);
        log.info("Пользователь добавлен в хранилище под id: {}", id);

        return user;
    }

    public User updateUser(User user) {
        log.info("Поступил запрос на обновление данных о пользователе под id: {}, c данными: name: {} и email: {}",
                user.getId(), user.getName(), user.getEmail());
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
        log.info("Поступил запрос на удаление пользователя с id: {}", id);
        if (hasInStorage(id)) {
            freeIdList.add(id);
            log.info("Пользователь с id: {} удален", id);
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
