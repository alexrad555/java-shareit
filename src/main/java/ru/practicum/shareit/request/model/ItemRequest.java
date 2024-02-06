package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;
}
