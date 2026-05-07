package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserRequestDto {
    private String name;
    private String email;

    public boolean isNameNotNull() {
        return name != null;
    }

    public boolean isEmailNotNull() {
        return email != null;
    }
}
