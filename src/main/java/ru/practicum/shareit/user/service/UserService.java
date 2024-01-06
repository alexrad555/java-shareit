package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User getById(Long userId);

    User update(User user, Long userId);

    List<User> getAll();

    void delete(Long userId);
}
