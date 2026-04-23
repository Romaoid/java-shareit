package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    @Query("select b.id as id, " +
            "b.startDate as startDate, " +
            "b.endDate as endDate, " +
            "b.status as status, " +
            "b.item.id as itemId, " +
            "b.item.name as itemName, " +
            "b.booker.id as bookerId, " +
            "b.booker.name as bookerName " +
            "from Booking b where b.id = ?1")
    BookingView findBookViewById(Long id);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.booker.id = ?1 " +
            "and b.item.id = ?2")
    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.id = ?1")
    Booking findBookingById(long id);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.booker.id = ?1 " +
            "order by b.startDate desc")
    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.booker.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.startDate desc")
    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDateDesc(Long bookerId, String status);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.startDate <= CURRENT_TIMESTAMP " +
            "and b.endDate >= CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findAllCurrentByBookerId(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.endDate < CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findAllPastByBookerId(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.startDate > CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findAllFutureByBookerId(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.item.owner = ?1 " +
            "order by b.startDate desc")
    List<Booking> findAllByOwnerIdOrderByStartDateDesc(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.item.owner = ?1 " +
            "and b.status = ?2 " +
            "order by b.startDate desc")
    List<Booking> findAllByOwnerIdAndStatusIsOrderByStartDateDesc(Long ownerId, String status);

    @Query("SELECT b.item.id, MAX(b.endDate) FROM Booking b " +
            "WHERE b.item.owner = ?1 " +
            "AND b.status = 'APPROVED' " +
            "AND b.endDate < CURRENT_TIMESTAMP " +
            "GROUP BY b.item.id")
    List<Object[]> findLastBookingDatesByOwnerId(Long ownerId);

    @Query("SELECT MAX(b.endDate) FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.status = 'APPROVED' " +
            "AND b.endDate < CURRENT_TIMESTAMP")
    Optional<LocalDateTime> findLastBookingDateByItemId(Long itemId);

    @Query("SELECT b.item.id, MIN(b.startDate) FROM Booking b " +
            "WHERE b.item.owner = ?1 " +
            "AND b.status = 'APPROVED' " +
            "AND b.startDate > CURRENT_TIMESTAMP " +
            "GROUP BY b.item.id ")
    List<Object[]> findNextBookingDatesByOwnerId(Long ownerId);

    @Query("SELECT MIN(b.startDate) FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.status = 'APPROVED' " +
            "AND b.startDate > CURRENT_TIMESTAMP")
    Optional<LocalDateTime> findNextBookingDateByItemId(Long itemId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.item.owner = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.startDate <= CURRENT_TIMESTAMP " +
            "and b.endDate >= CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findAllCurrentByOwnerId(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.item.owner = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.endDate < CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findAllPastByOwnerId(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item " +
            "join fetch b.booker " +
            "where b.item.owner= ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.startDate > CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findAllFutureByOwnerId(Long ownerId);
}
