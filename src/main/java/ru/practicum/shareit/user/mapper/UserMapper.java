package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequestCreate;
import ru.practicum.shareit.user.dto.UserRequestUpdate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }

    public static User mapUserFromCreateReq(UserRequestCreate userRequestCreate) {
        User user = new User();

        user.setName(userRequestCreate.getName());
        user.setEmail(userRequestCreate.getEmail());

        return user;
    }

    public static User mapUserFromUpdateReq(UserRequestUpdate userRequestUpdate) {
        User user = new User();

        if (userRequestUpdate.isNameNotNull()) {
            user.setName(userRequestUpdate.getName());
        }
        if (userRequestUpdate.isEmailNotNull()) {
            user.setEmail(userRequestUpdate.getEmail());
        }

        return user;
    }
}
