package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
public class ItemRequest {
    @NotNull
    @Size(min = 1)
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private final Boolean available;
}
