package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.OffsetPageable;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreate;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequest create(Long userId, ItemRequestCreate request) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(request);
        User user = userService.findById(userId);
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity();
        itemRequestEntity.setDescription(request.getDescription());
        itemRequestEntity.setRequestor(user);
        return itemRequestMapper.toRequest(itemRequestRepository.save(itemRequestEntity));
    }

    @Override
    public List<ItemRequest> findAllByOwnUserId(Long userId, int from, int size) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(from);
        Objects.requireNonNull(size);
        userService.findById(userId);
        Pageable pageable = new OffsetPageable(from, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> itemRequests = itemRequestMapper.toRequest(itemRequestRepository.findAllByRequestorId(userId, pageable));
        fillRequestItems(itemRequests);
        return itemRequests;
    }

    @Override
    public List<ItemRequest> findAllByOtherUserId(Long userId, int from, int size) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(from);
        Objects.requireNonNull(size);
        userService.findById(userId);
        Pageable pageable = new OffsetPageable(from, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> itemRequests = itemRequestMapper.toRequest(itemRequestRepository.findAllByUserIdNot(userId, pageable));
        fillRequestItems(itemRequests);
        return itemRequests;
    }

    @Override
    public Optional<ItemRequest> findOptionalById(Long requestId) {
        if (requestId == null) {
            return Optional.ofNullable(null);
        }
        return itemRequestRepository.findById(requestId)
                .map(itemRequestMapper::toRequest);
    }


    @Override
    public ItemRequest findById(Long userId, Long requestId) {
        userService.findById(userId);
        return findOptionalById(requestId)
         .map(this::fillRequestItems)
                .orElseThrow(
                        () -> new DataNotFoundException(String.format("Запрос с id %d не найден", requestId)));
    }

    private void fillRequestItems(List<ItemRequest> itemRequests) {
        List<Long> ids = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemMapper.toItem(itemRepository.findAllByRequestIdIn(ids));
        Map<Long, List<Item>> itemMap = items.stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId(), Collectors.mapping(Function.identity(), Collectors.toList())));
        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItems(itemMap.getOrDefault(itemRequest.getId(), Collections.EMPTY_LIST));
        }
    }

    private ItemRequest fillRequestItems(ItemRequest itemRequest) {
        fillRequestItems(List.of(itemRequest));
        return itemRequest;
    }

//    private void checkPageSize(Long from, Long size) {
//        if (from < 0 || size <= 0) {
//            throw new ValidationException("Не корректные данные");
//        }
//    }
}
