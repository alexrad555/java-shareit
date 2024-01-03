package ru.practicum.shareit.user.storage.memory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.DataNotFoundException;
import ru.practicum.shareit.user.exception.DuplicateException;
import ru.practicum.shareit.user.exception.InternalServerErrorException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> storage = new HashMap<>();
    private Long generatedId = 0L;

    @Override
    public User create(User user) {
        if (user.getId() != null) {
            throw new InternalServerErrorException("не должен приходить id");
        }
        if (user.getEmail() == null) {
            throw new InternalServerErrorException("должен приходить email");
        }
        if (checkEmailOnStorage(user, null)) {
            throw new DuplicateException("не должен повторяться email");
        }
        user.setId(++generatedId);
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User userDto, Long userId) {
        if (userId == null) {
            throw new InternalServerErrorException("не приходить id");
        }
        if (!storage.containsKey(userId)) {
            throw new DataNotFoundException("Не найден id");
        }
        if (userDto.getEmail() != null && checkEmailOnStorage(userDto, userId)) {
            throw new DuplicateException("не должен повторяться email");
        }
        User changeUser = storage.get(userId);
        if (userDto.getEmail() != null) {
            changeUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            changeUser.setName(userDto.getName());
        }
        storage.put(userId, changeUser);
        return changeUser;
    }

    @Override
    public User getById(Long id) {
        if (!storage.containsKey(id)) {
            throw new DataNotFoundException("Не найден id");
        }
        return storage.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(Long userId) {
        if (!storage.containsKey(userId)) {
            throw new DataNotFoundException("Не найден id");
        }
        storage.remove(userId);
    }

    private boolean checkEmailOnStorage(User userDto, Long userId) {
        if (userId != null) {
            User currentUser = storage.get(userId);
            if (userDto.getEmail().equals(currentUser.getEmail())) {
                return false;
            }
        }
        for (User userStorage : storage.values()) {
            if (userStorage.getEmail().equals(userDto.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
