package ru.practicum.shareit.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    private final Long id;
    private final String name;
    @Email
    private final String email;
}
