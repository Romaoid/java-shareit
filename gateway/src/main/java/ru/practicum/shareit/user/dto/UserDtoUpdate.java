package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDtoUpdate {
    private String name;

    @Email(message = "Invalid email. Example: Login1@domain.com")
    private String email;
}
