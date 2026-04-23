package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    @NotNull(message = "itemId is required")
    private Long itemId;

    private Long bookerId;

    @NotNull(message = "start date is required")
    @FutureOrPresent(message = "start date should be today or in the future")
    private LocalDateTime start;

    @NotNull(message = "end date is required")
    @Future(message = "end date should be in the future")
    private LocalDateTime end;
}
