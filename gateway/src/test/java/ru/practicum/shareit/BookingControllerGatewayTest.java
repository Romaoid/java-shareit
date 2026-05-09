package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingControllerGateway;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingControllerGateway.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerGatewayTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void addBooking_shouldReturn200WhenBookingIsValid() throws Exception {
        BookingDtoCreate request = new BookingDtoCreate();
        request.setItemId(1L);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        when(bookingClient.addBooking(anyLong(), any(BookingDtoCreate.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void addBooking_shouldReturn400WhenItemIdIsNull() throws Exception {
        BookingDtoCreate request = new BookingDtoCreate();
        request.setItemId(null);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_shouldReturn400WhenStartDateIsNull() throws Exception {
        BookingDtoCreate request = new BookingDtoCreate();
        request.setItemId(1L);
        request.setStart(null);
        request.setEnd(now.plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_shouldReturn400WhenEndDateIsNull() throws Exception {
        BookingDtoCreate request = new BookingDtoCreate();
        request.setItemId(1L);
        request.setStart(now.plusDays(1));
        request.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_shouldReturn400WhenStartDateIsInPast() throws Exception {
        BookingDtoCreate request = new BookingDtoCreate();
        request.setItemId(1L);
        request.setStart(now.minusDays(1));
        request.setEnd(now.plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
