package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.controller.dto.ItemRequestCreate;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequest create(Long userId, ItemRequestCreate request);

    List<ItemRequest> findAllByOwnUserId(Long userId, int from, int size);

    List<ItemRequest> findAllByOtherUserId(Long userId, int from, int size);

    Optional<ItemRequest> findOptionalById(Long requestId);

    ItemRequest findById(Long userId, Long requestId);
}
