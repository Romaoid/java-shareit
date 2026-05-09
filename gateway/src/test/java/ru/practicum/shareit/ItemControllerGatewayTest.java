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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemControllerGateway;
import ru.practicum.shareit.item.dto.ItemDtoCreate;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemControllerGateway.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerGatewayTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void addItem_shouldReturn200WhenItemIsValid() throws Exception {
        ItemDtoCreate request = new ItemDtoCreate();
        request.setName("Drill");
        request.setDescription("Powerful drill");
        request.setAvailable(true);

        when(itemClient.addItem(any(Long.class), any(ItemDtoCreate.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void addItem_shouldReturn400WhenNameIsBlank() throws Exception {
        ItemDtoCreate request = new ItemDtoCreate();
        request.setName("");
        request.setDescription("Powerful drill");
        request.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_shouldReturn400WhenDescriptionIsBlank() throws Exception {
        ItemDtoCreate request = new ItemDtoCreate();
        request.setName("Drill");
        request.setDescription("");
        request.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_shouldReturn400WhenAvailableIsNull() throws Exception {
        ItemDtoCreate request = new ItemDtoCreate();
        request.setName("Drill");
        request.setDescription("Powerful drill");
        request.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
