package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class BookingControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private Long bookerId;
    private Long ownerId;
    private Long itemId;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() throws Exception {
        UserRequestDto owner = new UserRequestDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        String ownerResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ownerId = objectMapper.readTree(ownerResponse).get("id").asLong();

        UserRequestDto booker = new UserRequestDto();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        String bookerResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booker)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        bookerId = objectMapper.readTree(bookerResponse).get("id").asLong();

        ItemRequestDto item = new ItemRequestDto();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        String itemResponse = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        itemId = objectMapper.readTree(itemResponse).get("id").asLong();
    }

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        String bookingResponse = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("id").asLong();

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getUserBookings_shouldReturnBookings() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}
