package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemCreateRequest {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
