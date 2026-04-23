package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

public interface BookingView {
    Long getId();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    BookingStatus getStatus();
    Long getItemId();
    String getItemName();
    Long getBookerId();
    String getBookerName();
}
