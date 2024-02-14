package ru.practicum.shareit.user.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    static User itemOwner;

    static User itemRequestor;

    @BeforeAll
    void setup() {
        itemOwner = new User(1L, "Tod", "user@user.com");
        itemRequestor = new User(2L, "Bob", "user2@user.com");
        userRepository.save(itemOwner);
        userRepository.save(itemRequestor);
    }

    @Test
    void canFindByEmail() {
        Optional<User> user = userRepository.findByEmail("user2@user.com");
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.get()).isEqualTo(itemRequestor);
    }

}