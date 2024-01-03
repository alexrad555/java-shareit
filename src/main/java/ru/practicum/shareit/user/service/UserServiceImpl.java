package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User create(User user) {
//        if (user.getName() == null || user.getName().isBlank()) {
//            user.setName(user.getLogin());
//        }
        return userStorage.create(user);
    }

    @Override
    public User getById(Long userId) {
        return userStorage.getById(userId);
    }

    @Override
    public User update(User user, Long userId) {
        return userStorage.update(user, userId);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(userId);
    }
}
