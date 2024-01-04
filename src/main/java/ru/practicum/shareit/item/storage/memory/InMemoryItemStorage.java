package ru.practicum.shareit.item.storage.memory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.DataNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> storage = new HashMap<>();
    private Long generatedId = 0L;

    @Override
    public Item create(Item itemDto, User user) {
        if (itemDto.getId() != null) {
            throw new ValidationException("не должен приходить id");
        }
        itemDto.setId(++generatedId);
        itemDto.setOwner(user);
        storage.put(itemDto.getId(), itemDto);
        return storage.get(itemDto.getId());
    }

    @Override
    public Item update(Item itemDto, User user, Long itemId) {
        if (itemId == null) {
            throw new ValidationException("не приходить id");
        }
        Item item = storage.get(itemId);
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new DataNotFoundException("нет прав на изменение");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    @Override
    public Item getById(Long itemId) {
        Item item = storage.get(itemId);
        if (item == null) {
            throw new DataNotFoundException("Не найден id");
        }
        return item;
    }

    @Override
    public List<Item> getAll(Long userId) {
        return storage.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }
}
