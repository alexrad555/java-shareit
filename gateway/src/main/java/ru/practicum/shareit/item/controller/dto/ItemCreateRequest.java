package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateRequest {
    @NotNull
    @Size(min = 1)
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;

    private Long requestId;
}
