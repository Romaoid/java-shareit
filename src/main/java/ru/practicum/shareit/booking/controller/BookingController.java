package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.constants.HttpHeaders;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long bookerId,
                                 @RequestBody BookingRequest request) {
        return bookingService.addBooking(bookerId, request);
    }

    @PatchMapping("/{bookingId:\\d+}")
    public BookingDto updateBookingStatus(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long ownerId,
                                          @PathVariable("bookingId") long bookingId,
                                          @NotBlank @RequestParam("approved") String newStatus) {
        return bookingService.updateBookingStatus(ownerId, bookingId, newStatus);
    }

    @GetMapping("/{bookingId:\\d+}")
    public BookingDto getBooking(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                                 @PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getBookingsByBookerId(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long bookerId,
                                                  @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerId(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long ownerId,
                                                  @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByOwnerId(ownerId, state);
    }
}
