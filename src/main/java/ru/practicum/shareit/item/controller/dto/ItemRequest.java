package ru.practicum.shareit.item.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemRequest {
    @NotNull
    @Size(min = 1)
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
}
