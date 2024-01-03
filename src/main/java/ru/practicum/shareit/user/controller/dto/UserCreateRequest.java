package ru.practicum.shareit.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class UserCreateRequest {

    @NotNull
    private final String name;

    @NotNull
    @Email
    private final String email;

}
