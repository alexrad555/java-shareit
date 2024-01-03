package ru.practicum.shareit.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private final Long id;
    private final String name;
    private final String email;
}
