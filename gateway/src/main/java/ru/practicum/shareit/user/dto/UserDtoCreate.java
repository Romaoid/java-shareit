package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDtoCreate {
    private String name;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email. Example: Login1@domain.com")
    private String email;
}
