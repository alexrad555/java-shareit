package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.controller.dto.UserCreateRequest;
import ru.practicum.shareit.user.controller.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable Long userId) {
        return mapper.toResponse(service.getById(userId));
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return service.getAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        User user = mapper.toUser(request);
        return mapper.toResponse(service.create(user));
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest userDto) {
        User user = mapper.toUserUpdate(userDto);
        User userRes = service.update(user, userId);
        return mapper.toResponse(userRes);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        service.delete(userId);
    }
}
