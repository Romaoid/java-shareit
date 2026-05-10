package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingView;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidateException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private BookingService bookingService;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void addBooking_shouldCreateBookingSuccessfully() {
        long bookerId = 1L;
        long ownerId = 2L;
        long itemId = 1L;

        Item item = new Item();
        item.setId(itemId);
        item.setDescription("test");
        item.setName("Drill");
        item.setOwner(ownerId);
        item.setAvailable(true);

        User booker = new User();
        booker.setId(bookerId);
        booker.setName("John Doe");

        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setBooker(booker);
        savedBooking.setItem(item);
        savedBooking.setStartDate(request.getStart());
        savedBooking.setEndDate(request.getEnd());
        savedBooking.setStatus(BookingStatus.WAITING);

        BookingView bookingView = mock(BookingView.class);
        when(bookingView.getId()).thenReturn(1L);
        when(bookingView.getStartDate()).thenReturn(request.getStart());
        when(bookingView.getEndDate()).thenReturn(request.getEnd());
        when(bookingView.getStatus()).thenReturn(BookingStatus.WAITING);
        when(bookingView.getItemId()).thenReturn(itemId);
        when(bookingView.getItemName()).thenReturn("Drill");
        when(bookingView.getBookerId()).thenReturn(bookerId);
        when(bookingView.getBookerName()).thenReturn("John Doe");

        when(userStorage.existsById(bookerId))
                .thenReturn(true);
        when(userStorage.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(itemStorage.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(savedBooking);
        when(bookingStorage.findBookViewById(1L))
                .thenReturn(bookingView);

        BookingDto result = bookingService.addBooking(bookerId, request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("WAITING");

        verify(bookingStorage, times(1)).save(any(Booking.class));
    }

    @Test
    void addBooking_shouldThrowExceptionWhenItemNotFound() {
        long bookerId = 1L;
        long itemId = 999L;

        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        when(userStorage.existsById(bookerId))
                .thenReturn(true);
        when(itemStorage.findById(itemId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.addBooking(bookerId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("item not found");

        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_shouldThrowExceptionWhenUserBooksOwnItem() {
        long bookerId = 1L;
        long itemId = 1L;

        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(bookerId);
        item.setAvailable(true);

        when(userStorage.existsById(bookerId))
                .thenReturn(true);
        when(itemStorage.findById(itemId))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.addBooking(bookerId, request))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("cant book his item");

        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_shouldThrowExceptionWhenItemUnavailable() {
        long bookerId = 1L;
        long itemId = 1L;

        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(2L);
        item.setAvailable(false);

        when(userStorage.existsById(bookerId))
                .thenReturn(true);
        when(itemStorage.findById(itemId))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.addBooking(bookerId, request))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("item is unavailable");

        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_shouldApproveBookingSuccessfully() {
        long ownerId = 2L;
        long bookingId = 1L;
        String newStatus = "approved";

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);

        Item item = new Item();
        item.setOwner(ownerId);
        booking.setItem(item);

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingView bookingView = mock(BookingView.class);

        when(bookingView.getId())
                .thenReturn(bookingId);

        when(bookingView.getStartDate())
                .thenReturn(now);

        when(bookingView.getEndDate())
                .thenReturn(now.plusDays(1));

        when(bookingView.getStatus())
                .thenReturn(BookingStatus.APPROVED);

        when(bookingView.getItemId())
                .thenReturn(1L);

        when(bookingView.getItemName())
                .thenReturn("Drill");

        when(bookingView.getBookerId())
                .thenReturn(1L);

        when(bookingView.getBookerName())
                .thenReturn("John");

        when(bookingStorage.findBookViewById(bookingId))
                .thenReturn(bookingView);

        BookingDto result = bookingService.updateBookingStatus(ownerId, bookingId, newStatus);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("APPROVED");

        verify(bookingStorage, times(1)).save(booking);
    }

    @Test
    void updateBookingStatus_shouldThrowExceptionWhenUserIsNotOwner() {
        long ownerId = 2L;
        long wrongUserId = 3L;
        long bookingId = 1L;
        String newStatus = "approved";

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);

        Item item = new Item();
        item.setOwner(ownerId);
        booking.setItem(item);

        when(bookingStorage.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBookingStatus(wrongUserId, bookingId, newStatus))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("isn't owner");

        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_shouldThrowExceptionWhenBookingAlreadyHasStatus() {
        long ownerId = 2L;
        long bookingId = 1L;
        String newStatus = "approved";

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);

        Item item = new Item();
        item.setOwner(ownerId);
        booking.setItem(item);

        when(bookingStorage.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBookingStatus(ownerId, bookingId, newStatus))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("already have status");

        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void getBookingsByBookerId_shouldReturnBookingsWithStateAll() {
        long bookerId = 1L;
        String state = "ALL";

        User booker = new User();
        booker.setId(bookerId);
        booker.setName("John");

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartDate(now);
        booking.setEndDate(now.plusDays(1));
        booking.setStatus(BookingStatus.WAITING);

        when(userStorage.existsById(bookerId))
                .thenReturn(true);
        when(bookingStorage.findAllByBookerIdOrderByStartDateDesc(bookerId))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByBookerId(bookerId, state);

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        assertThat(result.getFirst().getId()).isEqualTo(1L);

        verify(bookingStorage, times(1))
                .findAllByBookerIdOrderByStartDateDesc(bookerId);
    }

    @Test
    void getBookingsByBookerId_shouldThrowExceptionWhenUserNotFound() {
        long bookerId = 999L;
        String state = "ALL";

        when(userStorage.existsById(bookerId))
                .thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingsByBookerId(bookerId, state))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("user not found");
    }

    @Test
    void getBookingsByOwnerId_shouldReturnBookingsForOwner() {
        long ownerId = 2L;
        String state = "ALL";

        User booker = new User();
        booker.setId(1L);
        booker.setName("John");

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setOwner(ownerId);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartDate(now);
        booking.setEndDate(now.plusDays(1));
        booking.setStatus(BookingStatus.WAITING);

        when(userStorage.existsById(ownerId))
                .thenReturn(true);
        when(itemStorage.existsByOwner(ownerId))
                .thenReturn(true);
        when(bookingStorage.findAllByOwnerIdOrderByStartDateDesc(ownerId))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByOwnerId(ownerId, state);

        assertThat(result)
                .isNotNull()
                .hasSize(1);

        assertThat(result.getFirst().getId()).isEqualTo(1L);

        verify(bookingStorage, times(1)).findAllByOwnerIdOrderByStartDateDesc(ownerId);
    }

    @Test
    void getBookingById_shouldReturnBookingWhenUserIsBooker() {
        long userId = 1L;
        long bookingId = 1L;

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStartDate(now);
        booking.setEndDate(now.plusDays(1));
        booking.setStatus(BookingStatus.WAITING);

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(bookingStorage.findById(bookingId))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(userId, bookingId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookingId);
    }

    @Test
    void getBookingById_shouldThrowExceptionWhenUserIsNotOwnerOrBooker() {
        long userId = 3L;
        long bookingId = 1L;

        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);

        when(userStorage.existsById(userId))
                .thenReturn(true);
        when(bookingStorage.findById(bookingId))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(userId, bookingId))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("allow only to item's owner and booker");
    }
}
