package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDtoCreate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userDtoCreate_shouldSerializeAndDeserialize() throws Exception {
        UserDtoCreate dto = new UserDtoCreate();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");

        String json = objectMapper.writeValueAsString(dto);
        UserDtoCreate result = objectMapper.readValue(json, UserDtoCreate.class);

        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void userDtoCreate_shouldHandleNullValues() throws Exception {
        UserDtoCreate dto = new UserDtoCreate();
        dto.setName(null);
        dto.setEmail(null);

        String json = objectMapper.writeValueAsString(dto);
        UserDtoCreate result = objectMapper.readValue(json, UserDtoCreate.class);

        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void itemDtoCreate_shouldSerializeAndDeserialize() throws Exception {
        ItemDtoCreate dto = new ItemDtoCreate();
        dto.setName("Drill");
        dto.setDescription("Powerful drill");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        String json = objectMapper.writeValueAsString(dto);
        ItemDtoCreate result = objectMapper.readValue(json, ItemDtoCreate.class);

        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(1L);
    }

    @Test
    void bookingDtoCreate_shouldSerializeLocalDateTime() throws Exception {
        BookingDtoCreate dto = new BookingDtoCreate();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2025, 12, 25, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2025, 12, 26, 10, 0, 0));

        String json = objectMapper.writeValueAsString(dto);
        BookingDtoCreate result = objectMapper.readValue(json, BookingDtoCreate.class);

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo("2025-12-25T10:00:00");
        assertThat(result.getEnd()).isEqualTo("2025-12-26T10:00:00");
    }

    @Test
    void newRequestDto_shouldSerializeAndDeserialize() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need a drill");

        String json = objectMapper.writeValueAsString(dto);
        ItemRequestDto result = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(result.getDescription()).isEqualTo("Need a drill");
    }
}
