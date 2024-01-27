package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        if (user.getId() != null) {
            throw new ValidationException("не должен приходить id");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("должен приходить email");
        }
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new DataNotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    @Override
    public User update(User userDto) {
        if (userDto.getId() == null) {
            throw new ValidationException("не приходить id");
        }
        User changeUser = findById(userDto.getId());
        String newEmail = userDto.getEmail();
        if (newEmail != null) {
            User emailUser = findByEmail(newEmail);
            if (emailUser != null && emailUser.getId().equals(changeUser.getId())) {
                throw new DuplicateException("не должен повторяться email");
            }
            changeUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            changeUser.setName(userDto.getName());
        }
        return userRepository.save(changeUser);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    @Modifying
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }
}
