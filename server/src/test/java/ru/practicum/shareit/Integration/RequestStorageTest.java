package ru.practicum.shareit.Integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.dao.RequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class RequestStorageTest {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private Long authorId;

    @BeforeEach
    void setUp() {
        User author = new User();
        author.setName("Author");
        author.setEmail("author@example.com");
        authorId = userStorage.save(author).getId();
    }

    @Test
    void findAllByAuthorId_shouldReturnRequestsByAuthor() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setCreated(Instant.now());
        request.setAuthor(userStorage.getReferenceById(authorId));
        requestStorage.save(request);

        List<ItemRequest> result = requestStorage.findAllByAuthorId(authorId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void findAllByAuthorIdIsNot_shouldReturnRequestsFromOtherUsers() {
        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        User otherUserSaved = userStorage.save(otherUser);

        assertThat(otherUserSaved)
                .isNotNull();

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setCreated(Instant.now());
        request.setAuthor(otherUserSaved);
        requestStorage.save(request);

        assertThat(authorId).isNotEqualTo(otherUserSaved.getId());

        List<ItemRequest> result = requestStorage.findAllByAuthorIdIsNot(authorId);

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        assertThat(result.getFirst().getAuthor().getId()).isEqualTo(otherUserSaved.getId());
    }

    @Test
    void existsById_shouldReturnTrueWhenRequestExists() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setCreated(Instant.now());
        request.setAuthor(userStorage.getReferenceById(authorId));
        ItemRequest saved = requestStorage.save(request);

        boolean exists = requestStorage.existsById(saved.getId());

        assertThat(exists).isTrue();
    }
}
