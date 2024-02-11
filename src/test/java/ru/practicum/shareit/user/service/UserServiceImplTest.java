package ru.practicum.shareit.user.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.controller.dto.UserCreateRequest;
import ru.practicum.shareit.user.controller.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                UserMapperImpl.class,
                UserServiceImpl.class
        }
)
class UserServiceImplTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    static User userFirst;

    static User userSecond;

    @BeforeAll
    static void beforeAll() {
        userFirst = new User(1L, "Tod", "user@user.com");
        userSecond = new User(2L, "Bob", "user2@user.com");
    }

    @Test
    void canCreateUser() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName("Tod");
        userCreateRequest.setEmail("user3@user.com");
        User user = userMapper.toUser(userCreateRequest);

        when(userRepository.save(any())).thenReturn(user);

        User userRes = userService.create(user);
        Assertions.assertThat(userRes.getName()).isEqualTo(user.getName());
        Assertions.assertThat(userRes.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void willThrowWhenCreateUserIdNotNull() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName("Tod");
        userCreateRequest.setEmail("user3@user.com");
        User user = userMapper.toUser(userCreateRequest);
        user.setId(1L);

        when(userRepository.save(any())).thenReturn(user);

        Assertions.assertThatThrownBy(
                        () -> userService.create(user))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void willThrowWhenCreateUserEmailIsNull() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName("Tod");
        userCreateRequest.setEmail(null);
        User user = userMapper.toUser(userCreateRequest);

        when(userRepository.save(any())).thenReturn(user);

        Assertions.assertThatThrownBy(
                        () -> userService.create(user))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void canFindById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userFirst));
        User user = userService.findById(userFirst.getId());
        Assertions.assertThat(user).isEqualTo(userFirst);

    }

    @Test
    void willThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(
                        () -> userService.findById(99L))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void canUpdateUser() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setId(1L);
        userUpdateRequest.setName("Update name");
        userUpdateRequest.setEmail("update@user.com");

        User userOrigin = userMapper.toUserUpdate(userUpdateRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userFirst));
        when(userRepository.save(any())).thenReturn(userOrigin);
        User userRes = userService.update(userOrigin);

        Assertions.assertThat(userRes.getId()).isEqualTo(userOrigin.getId());
        Assertions.assertThat(userRes.getName()).isEqualTo(userOrigin.getName());
        Assertions.assertThat(userRes.getEmail()).isEqualTo(userOrigin.getEmail());
    }

    @Test
    void willThrowWhenUpdateUserIdIsNull() {
        User user = new User();
        user.setId(null);
        Assertions.assertThatThrownBy(
                        () -> userService.update(user))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void canFindAll() {
        List<User> userList = List.of(userFirst, userSecond);
        when(userRepository.findAll()).thenReturn(userList);
        List<User> userListRes = userService.findAll();
        Assertions.assertThat(userListRes.size()).isEqualTo(userList.size());
    }

    @Test
    void canDeleteById() {
        User user = new User();
        user.setId(3L);
        user.setEmail("Tod");
        user.setEmail("user@user.com");

        userService.deleteById(user.getId());
    }

    @Test
    void willThrowWhenDuplicateEmail() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setId(1L);
        userUpdateRequest.setName("Update name");
        userUpdateRequest.setEmail("update@user.com");
        User userOrigin = userMapper.toUserUpdate(userUpdateRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userFirst));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userSecond));

        Assertions.assertThatThrownBy(
                        () -> userService.update(userOrigin))
                .isInstanceOf(DuplicateException.class);
    }
}