package ru.practicum.shareit.request.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ItemRequestCreate {

    @NotNull
    private String description;

}
