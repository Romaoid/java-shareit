package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserStorage extends JpaRepository<User, Long> {

    User findUserById(Long id);

    List<User> findAll();

    User save(User user);

    void deleteById(Long id);
}
