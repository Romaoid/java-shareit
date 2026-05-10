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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class BookingService {
    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;

    @Transactional
    public BookingDto addBooking(Long bookerId, BookingRequest request) {
        request.setBookerId(bookerId);

        validateBookingRequest(request);

        Booking booking = BookingMapper.mapRequestToBooking(request);

        User booker = userStorage.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemStorage.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        booking.setBooker(booker);
        booking.setItem(item);

        Booking newBooking = bookingStorage.save(booking);

        return BookingMapper.mapToBookingDto(
                bookingStorage.findBookViewById(
                        newBooking.getId()
                ));
    }

    @Transactional
    public BookingDto updateBookingStatus(Long ownerId, Long bookingId, String newStatus) {
        Booking updatedBooking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("error during status validation"));

        if (updatedBooking.getStatus() != BookingStatus.WAITING) {
            throw new ValidateException("item already have status: " + updatedBooking.getStatus());
        }

        if (!Objects.equals(updatedBooking.getItem().getOwner(), ownerId)) {
            throw new ValidateException("User isn't owner this item");
        }

        BookingStatus status = switch (newStatus.trim().toUpperCase()) {
            case "APPROVED", "TRUE" -> BookingStatus.APPROVED;
            case "REJECTED", "FALSE" -> BookingStatus.REJECTED;
            default -> throw new ValidateException("approved parameter should be approved/rejected");
        };

        updatedBooking.setStatus(status);

        updatedBooking = bookingStorage.save(updatedBooking);

        return BookingMapper.mapToBookingDto(
                bookingStorage.findBookViewById(
                        updatedBooking.getId()
                ));
    }

    public BookingDto getBookingById(Long userId, Long bookingId) {
        validateUserExisting(userId);

        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
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

        return bookingList
                .stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
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

    private void validateBookingRequest(BookingRequest request) {
        validateUserExisting(request.getBookerId());

        //NPE
        if (request.getItemId() == null) {
            throw new ValidateException("invalid itemId");
        }

        //Наличие в бд
        Item item = itemStorage.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("item not found"));

        //User не шарил item
        if (Objects.equals(
                item.getOwner(),
                request.getBookerId())) {
            throw new ValidateException("User cant book his item");
        }

        //Available == true
        if (item.getAvailable() == false) {
            throw new ValidateException("item is unavailable");
        }
    }

    private void validateUserIsOwnerOrBooker(long userId, long bookerId, long ownerId) {
        if ((!Objects.equals(userId, bookerId)) && (!Objects.equals(userId, ownerId))) {
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
        if (!itemStorage.existsByOwner(id)) {
            throw new ValidateException("User " + id + " don't share items yet");
        }
    }
}
