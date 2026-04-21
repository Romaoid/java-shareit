package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestUpdate {
    private String name;

    @Pattern(regexp = "\\S*", message = "Invalid email. Example: Login1@domain.com")
    private String email;

    public boolean isNameNotNull() {
        return name != null;
    }

    public boolean isEmailNotNull() {
        return email != null;
    }
}
