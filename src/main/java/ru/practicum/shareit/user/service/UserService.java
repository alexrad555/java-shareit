package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User findById(Long userId);

    User update(User user);

    List<User> findAll();

    void deleteById(Long userId);
}
