package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidateException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.user.dao.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class BookingService {
    @Autowired
    private BookingStorage bookingStorage;
    private ItemStorage itemStorage;
    private UserStorage userStorage;

    @Transactional
    public BookingDto addBooking(Long bookerId, BookingRequest request) {
        validateUserExisting(bookerId);
        validateItemExisting(request.getItemId());
        validateBookerIsNtOwner(bookerId, request.getItemId());
        validateDates(request.getStart(), request.getEnd());
        validateItemAvailable(request.getItemId());

        request.setBookerId(bookerId);

        Booking newBooking = bookingStorage.save(
                BookingMapper.mapRequestToBooking(request));

        return BookingMapper.mapToBookingDto(
                bookingStorage.findBookViewById(
                        newBooking.getId()
                ));
    }

    @Transactional
    public BookingDto updateBookingStatus(Long ownerId, Long bookingId, String newStatus) {
        validateUserExisting(ownerId);
        validateBookingExisting(bookingId);
        validateStatusIsWaiting(bookingId);
        validateUserIsOwner(ownerId, bookingId);

        BookingStatus status = switch (newStatus.trim().toUpperCase()) {
            case "APPROVED" -> BookingStatus.APPROVED;
            case "REJECTED" -> BookingStatus.REJECTED;
            default -> throw new ValidateException("approved parameter should be approved/rejected");
        };

        Booking updatedBooking = bookingStorage.getReferenceById(bookingId);

        updatedBooking.setStatus(status);

        updatedBooking = bookingStorage.save(updatedBooking);

        return BookingMapper.mapToBookingDto(
                bookingStorage.findBookViewById(
                        updatedBooking.getId()
                ));
    }

    public BookingDto getBookingById(Long userId, Long bookingId) {
        validateUserExisting(userId);
        validateBookingExisting(bookingId);

        Booking booking = bookingStorage.findBookingById(bookingId);
        validateUserIsOwnerOrBooker(userId, booking.getBooker().getId(), booking.getItem().getOwner());

        return BookingMapper.mapToBookingDto(booking);
    }

    public List<BookingDto> getBookingsByBookerId(Long bookerId, String state) {
        validateUserExisting(bookerId);
        BookingState bookingState = validateState(state);

        List<Booking> bookingList;
        switch (bookingState) {
            case ALL -> bookingList = bookingStorage.findAllByBookerIdOrderByStartDateDesc(bookerId);
            case WAITING, REJECTED -> bookingList =
                    bookingStorage.findAllByBookerIdAndStatusIsOrderByStartDateDesc(bookerId, bookingState.toString());
            case CURRENT -> bookingList = bookingStorage.findAllCurrentByBookerId(bookerId);
            case FUTURE -> bookingList = bookingStorage.findAllFutureByBookerId(bookerId);
            case PAST -> bookingList = bookingStorage.findAllPastByBookerId(bookerId);
            default -> bookingList = new ArrayList<>();
        }

        return bookingList.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    public List<BookingDto> getBookingsByOwnerId(Long ownerId, String state) {
        validateUserExisting(ownerId);
        validateUserShareItem(ownerId);
        BookingState bookingState = validateState(state);

        List<Booking> bookingList;
        switch (bookingState) {
            case ALL -> bookingList = bookingStorage.findAllByOwnerIdOrderByStartDateDesc(ownerId);
            case WAITING, REJECTED -> bookingList =
                    bookingStorage.findAllByOwnerIdAndStatusIsOrderByStartDateDesc(ownerId, bookingState.toString());
            case CURRENT -> bookingList = bookingStorage.findAllCurrentByOwnerId(ownerId);
            case FUTURE -> bookingList = bookingStorage.findAllFutureByOwnerId(ownerId);
            case PAST -> bookingList = bookingStorage.findAllPastByOwnerId(ownerId);
            default -> bookingList = new ArrayList<>();
        }

        return bookingList.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    private void validateUserExisting(Long id) {
        if (id == null) {
            throw new ValidateException("id isn't correct");
        }
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("user not found");
        }
    }

    private void validateItemExisting(Long id) {
        if (id == null) {
            throw new ValidateException("id isn't correct");
        }
        if (!itemStorage.existsById(id)) {
            throw new NotFoundException("item not found");
        }
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new ValidateException("The end date must be after the start date");
        }
    }

    private void validateItemAvailable(long id) {
        if (itemStorage.findItemById(id).getAvailable() != true) {
            throw new ValidateException("item is unavailable");
        }
    }

    private void validateBookingExisting(Long id) {
        if (id == null) {
            throw new ValidateException("id isn't correct");
        }
        if (!bookingStorage.existsById(id)) {
            throw new NotFoundException("booking not found");
        }
    }

    private void validateStatusIsWaiting(long id) {
        BookingStatus status = bookingStorage.findById(id)
                .orElseThrow(() -> new RuntimeException("error during status validation"))
                .getStatus();

        if (status != BookingStatus.WAITING) {
            throw new ValidateException("item already have status: " + status);
        }
    }

    private void validateBookerIsNtOwner(long userid, long itemId) {
        if (Objects.equals(itemStorage.findItemById(itemId).getOwner(), userid)) {
            throw new ValidateException("User cant book his item");
        }
    }

    private void validateUserIsOwner(long userid, long itemId) {
        if (!Objects.equals(itemStorage.findItemById(itemId).getOwner(), userid)) {
            throw new ValidateException("User isn't owner this item");
        }
    }

    private void validateUserIsOwnerOrBooker(long userId, long bookerId, long ownerId) {
        if ((!Objects.equals(userId, bookerId)) || (!Objects.equals(userId, ownerId))) {
            throw new ValidateException("This function allow only to item's owner and booker");
        }
    }

    private BookingState validateState(String state) {
        switch (state.trim().toUpperCase()) {
            case "ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED" -> {
                return BookingState.valueOf(state.trim().toUpperCase());
            }
            default -> throw new ValidateException("state parameter isn't correct");
        }
    }

    private void validateUserShareItem(long id) {
        if (itemStorage.findItemsByUserId(id).isEmpty()) {
            throw new ValidateException("User " + id + " don't share items yet");
        }
    }
}
