package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HttpHeaders;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingControllerGateway {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerId(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long bookerId,
                                                        @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingClient.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long ownerId,
                                                       @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingClient.getBookingsByOwnerId(ownerId, state);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long userId,
                                             @PathVariable("bookingId") long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long ownerId,
                                                      @PathVariable("bookingId") long bookingId,
                                                      @RequestParam("approved") String approved) {
        return bookingClient.updateBookingStatus(ownerId, bookingId, approved);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) long bookerId,
                                             @Valid @RequestBody BookingDtoCreate request) {
        return bookingClient.addBooking(bookerId, request);
    }
}
