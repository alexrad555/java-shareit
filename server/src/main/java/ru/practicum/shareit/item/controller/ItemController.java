package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemMapper mapper;
    private final ItemService itemService;
    private final CommentMapper commentMapper;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";


    @GetMapping("/{itemId}")
    public ItemResponse getById(@PathVariable Long itemId,
                                @RequestHeader(X_SHARER_USER_ID) Long userId) {
        Item item = itemService.findById(itemId, userId);
        return mapper.toResponse(item);
    }

    @GetMapping
    public List<ItemResponse> getAll(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                     @RequestParam(defaultValue = "0", required = false) int from,
                                     @RequestParam(defaultValue = "20", required = false) int size) {
        return mapper.toResponse(itemService.findAllByOwnerId(userId, from, size));
    }

    @GetMapping("/search")
    public List<ItemResponse> search(@RequestParam String text,
                                     @RequestHeader(X_SHARER_USER_ID) Long userId,
                                     @RequestParam(defaultValue = "0", required = false) int from,
                                     @RequestParam(defaultValue = "20", required = false) int size) {
        return mapper.toResponse(itemService.search(text, userId, from, size));
    }

    @PostMapping
    public ItemResponse create(@RequestBody ItemCreateRequest itemDto,
                               @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return mapper.toResponse(itemService.create(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@PathVariable Long itemId,
                               @RequestBody ItemUpdateRequest itemDto,
                               @RequestHeader(X_SHARER_USER_ID) Long userId) {
        Item item = mapper.toItemUpdate(itemDto);
        return mapper.toResponse((itemService.update(item, userId, itemId)));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@PathVariable Long itemId,
                                         @RequestBody CommentCreateRequest commentCreateRequest,
                                         @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return commentMapper.toResponse(itemService.createComment(itemId, commentCreateRequest, userId));
    }
}
