package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item create(Item itemDto, Long userId) {
        User user = userStorage.getById(userId);
        return itemStorage.create(itemDto, user);
    }

    @Override
    public Item getById(Long itemId) {
        return itemStorage.getById(itemId);
    }

    @Override
    public List<Item> search(String text) {
        return itemStorage.search(text);
    }

    @Override
    public Item update(Item itemDto, Long userId, Long itemId) {
        User user = userStorage.getById(userId);
        return itemStorage.update(itemDto, user, itemId);
    }

    @Override
    public List<Item> getAllUser(Long userId) {
        return itemStorage.getAll(userId);
    }
}
