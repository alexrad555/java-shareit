package ru.practicum.shareit.user.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class UserUpdateRequest {
    private Long id;
    private String name;
    @Email
    private String email;
}
