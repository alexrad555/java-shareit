package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                         @Valid @RequestBody ItemRequestCreate request) {
        log.info("Creating request {}, userId={}", request, userId);
        return itemRequestClient.create(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOwnRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all request by owner with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findAllOwnRequest(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOtherRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all request by other with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findAllOtherRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                           @PathVariable Long requestId) {
        log.info("Get request by id with userId={}, requestId={}", userId, requestId);
        return itemRequestClient.findById(userId, requestId);
    }
}
