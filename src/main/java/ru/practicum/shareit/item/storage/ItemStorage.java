package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item create(Item itemDto, User user);

    Item update(Item itemDto, User user, Long itemId);

    Item getById(Long itemId);

    List<Item> getAll(Long userId);

    List<Item> search(String text);
}
