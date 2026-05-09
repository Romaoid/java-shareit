package ru.practicum.shareit.Integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingStorageTest {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    private Long bookerId;
    private Long ownerId;
    private Long itemId;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        ownerId = userStorage.save(owner).getId();

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        bookerId = userStorage.save(booker).getId();

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(ownerId);
        itemId = itemStorage.save(item).getId();
    }

    @Test
    void findAllByBookerIdOrderByStartDateDesc_shouldReturnBookings() {
        // given
        Booking booking = new Booking();
        booking.setStartDate(now.plusDays(1));
        booking.setEndDate(now.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(userStorage.getReferenceById(bookerId));
        booking.setItem(itemStorage.getReferenceById(itemId));
        bookingStorage.save(booking);

        List<Booking> result = bookingStorage.findAllByBookerIdOrderByStartDateDesc(bookerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBooker().getId()).isEqualTo(bookerId);
    }

    @Test
    void findByBookerIdAndItemId_shouldReturnBooking() {
        Booking booking = new Booking();
        booking.setStartDate(now.plusDays(1));
        booking.setEndDate(now.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(userStorage.getReferenceById(bookerId));
        booking.setItem(itemStorage.getReferenceById(itemId));
        bookingStorage.save(booking);

        var result = bookingStorage.findByBookerIdAndItemId(bookerId, itemId);

        assertThat(result).isPresent();
        assertThat(result.get().getBooker().getId()).isEqualTo(bookerId);
        assertThat(result.get().getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void findAllByOwnerIdOrderByStartDateDesc_shouldReturnBookings() {
        Booking booking = new Booking();
        booking.setStartDate(now.plusDays(1));
        booking.setEndDate(now.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(userStorage.getReferenceById(bookerId));
        booking.setItem(itemStorage.getReferenceById(itemId));
        bookingStorage.save(booking);

        List<Booking> result = bookingStorage.findAllByOwnerIdOrderByStartDateDesc(ownerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getOwner()).isEqualTo(ownerId);
    }
}
