package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreate;
import ru.practicum.shareit.request.controller.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponse create(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                      @Valid @RequestBody ItemRequestCreate request) {
        return itemRequestMapper.toResponse(itemRequestService.create(userId, request));
    }

    @GetMapping
    public List<ItemRequestResponse> findAllOwnRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "0", required = false) int from,
                                                       @RequestParam(defaultValue = "20", required = false) int size) {
        return itemRequestMapper.toResponse(itemRequestService.findAllByOwnUserId(userId, from, size));
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> findAllOtherRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                         @RequestParam(defaultValue = "0", required = false) int from,
                                                         @RequestParam(defaultValue = "20", required = false) int size) {
        return itemRequestMapper.toResponse(itemRequestService.findAllByOtherUserId(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse findById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                        @PathVariable Long requestId) {
        return itemRequestMapper.toResponse(itemRequestService.findById(userId, requestId));
    }

}
