package ru.practicum.shareit.item.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentCreateRequest {
    @NotBlank
    private String text;
}
