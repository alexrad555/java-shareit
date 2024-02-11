package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.controller.dto.CommentCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(ItemCreateRequest itemDto, Long userId);

    Item findById(Long itemId, Long userId);

    List<Item> search(String text, Long userId, int from, int size);

    Item update(Item itemDto, Long userId, Long itemId);

    List<Item> findAllByOwnerId(Long userId, int from, int size);

    Comment createComment(Long itemId, CommentCreateRequest commentCreateRequest, Long userId);
}
