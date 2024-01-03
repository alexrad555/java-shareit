package ru.practicum.shareit.item.storage.memory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.DataNotFoundException;
import ru.practicum.shareit.user.exception.InternalServerErrorException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> storage = new HashMap<>();
    private Long generatedId = 0L;


    @Override
    public Item create(Item itemDto, User user) {
        if (itemDto.getId() != null) {
            throw new InternalServerErrorException("не должен приходить id");
        }
        itemDto.setId(++generatedId);
        itemDto.setOwner(user);
        storage.put(itemDto.getId(), itemDto);
        return storage.get(itemDto.getId());
    }

    @Override
    public Item update(Item itemDto, User user, Long itemId) {
        if (itemId == null) {
            throw new InternalServerErrorException("не приходить id");
        }
        Item item = storage.get(itemId);
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new DataNotFoundException("нет прав на измеение");
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
        if (!storage.containsKey(itemId)) {
            throw new DataNotFoundException("Не найден id");
        }
        return storage.get(itemId);
    }

    @Override
    public List<Item> getAll(Long userId) {
        List<Item> items = new ArrayList<>();
        for (Item item: storage.values()) {
            if (item.getOwner().getId().equals(userId)) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> items = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return items;
        }
        for (Item item: storage.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                items.add(item);
            }
        }
        return items;
    }
}
