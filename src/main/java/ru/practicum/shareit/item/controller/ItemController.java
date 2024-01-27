package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemMapper mapper;
    private final ItemService itemService;
    private final CommentMapper commentMapper;

    @GetMapping("/{itemId}")
    public ItemResponse getById(@PathVariable Long itemId,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemService.findById(itemId, userId);
        return mapper.toResponse(item);
    }

    @GetMapping
    public List<ItemResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllByOwnerId(userId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemResponse> search(@RequestParam String text,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.search(text, userId).stream()
                .map(mapper::toResponse)
                .collect(Collectors
                        .toList());
    }

    @PostMapping
    public ItemResponse create(@Valid @RequestBody ItemRequest itemDto,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = mapper.toItem(itemDto);
        return mapper.toResponse(itemService.create(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@PathVariable Long itemId,
                               @Valid @RequestBody ItemUpdateRequest itemDto,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = mapper.toItemUpdate(itemDto);
        return mapper.toResponse((itemService.update(item, userId, itemId)));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@PathVariable Long itemId,
                                         @Valid @RequestBody CommentCreateRequest commentCreateRequest,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return commentMapper.toResponse(itemService.createComment(itemId, commentCreateRequest, userId));
    }
}
