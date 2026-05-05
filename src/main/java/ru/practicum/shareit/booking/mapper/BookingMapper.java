package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingView;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapRequestToBooking(BookingRequest request) {
        Booking booking = new Booking();
        User booker = new User();
        Item item = new Item();

        booker.setId(request.getBookerId());
        item.setId(request.getItemId());

        booking.setStartDate(request.getStart());
        booking.setEndDate(request.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    public static BookingDto mapToBookingDto(BookingView view) {
        BookingDto dto = new BookingDto();

        dto.setId(view.getId());
        dto.setStart(view.getStartDate());
        dto.setEnd(view.getEndDate());
        dto.setItem(new ItemShortDto(view.getItemId(), view.getItemName()));
        dto.setBooker(new UserShortDto(view.getBookerId(), view.getBookerName()));
        dto.setStatus(view.getStatus().toString());

        return dto;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStartDate());
        dto.setEnd(booking.getEndDate());
        dto.setItem(new ItemShortDto(
                booking.getItem().getId(),
                booking.getItem().getName())
        );
        dto.setBooker(new UserShortDto(
                booking.getBooker().getId(),
                booking.getBooker().getName())
        );
        dto.setStatus(booking.getStatus().toString());

        return dto;
    }
}
