package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final UserStorage userStorage;

    @Test
    void findByEmail_shouldReturnUserWhenEmailExists() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        User saved = userStorage.save(user);

        var result = userStorage.findByEmail("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John Doe");
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void existsById_shouldReturnTrueWhenUserExists() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        User saved = userStorage.save(user);

        boolean exists = userStorage.existsById(saved.getId());

        assertThat(exists).isTrue();
    }
}
