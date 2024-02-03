package ru.practicum.shareit.item.controller.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ItemUpdateRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    public ItemUpdateRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }


}
