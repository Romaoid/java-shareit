package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestCreate {
    private String name;

    @NotNull(message = "Email is required")
    @Pattern(regexp = "\\S*", message = "Invalid email. Example: Login1@domain.com")
    private String email;
}
