package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user, Long userId);

    User getById(Long id);

    List<User> getAll();

    void delete(Long userId);
}
