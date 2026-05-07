package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestStorage extends JpaRepository<ItemRequest, Long> {

    @Query("select distinct r " +
            "from ItemRequest r " +
            "left join fetch r.items i " +
            "where r.author.id = ?1")
    List<ItemRequest> findAllByAuthorId(long id);

    @Query("select r " +
            "from ItemRequest r " +
            "join fetch r.items i " +
            "where r.author.id <> ?1")
    List<ItemRequest> findAllByAuthorIdIsNot(long id);

    @Modifying
    @Query(value = "insert into answers_requests (request_id, item_id) values (?1, ?2)",
            nativeQuery = true)
    void addItemToRequest(long requestId, long itemId);
}
