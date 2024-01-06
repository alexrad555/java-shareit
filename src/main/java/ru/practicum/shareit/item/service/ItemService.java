package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item itemDto, Long userId);

    Item getById(Long userId);

    List<Item> search(String text);

    Item update(Item itemDto, Long userId, Long itemId);

    List<Item> getAllUser(Long userId);
}
