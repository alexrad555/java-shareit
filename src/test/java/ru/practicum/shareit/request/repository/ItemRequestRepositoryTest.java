package ru.practicum.shareit.request.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.OffsetPageable;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    static User itemRequestor;
    static User itemRequestorSecond;

    ItemRequestEntity itemRequestEntityFirst;
    ItemRequestEntity itemRequestEntitySecond;
    ItemRequestEntity itemRequestEntityThird;

    @BeforeAll
    void setup() {
        itemRequestor = new User(1L, "Bob", "user1@user.com");
        itemRequestorSecond = new User(2L, "Tod", "user2@user.com");
        itemRequestEntityFirst = new ItemRequestEntity(1L, "Create Book", itemRequestor, LocalDateTime.now());
        itemRequestEntitySecond = new ItemRequestEntity(2L, "Drive car", itemRequestor, LocalDateTime.now());
        itemRequestEntityThird = new ItemRequestEntity(3L, "Write pen", itemRequestorSecond, LocalDateTime.now());
        userRepository.save(itemRequestor);
        userRepository.save(itemRequestorSecond);
        itemRequestRepository.save(itemRequestEntityFirst);
        itemRequestRepository.save(itemRequestEntitySecond);
        itemRequestRepository.save(itemRequestEntityThird);
    }

    @Test
    void canFindAllByRequestorId() {
        Pageable pageable = new OffsetPageable(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        List<ItemRequestEntity> itemRequestEntityList = itemRequestRepository.findAllByRequestorId(itemRequestor.getId(), pageable);
        Assertions.assertThat(itemRequestEntityList).isNotNull();
        Assertions.assertThat(itemRequestEntityList).containsExactlyInAnyOrder(itemRequestEntityFirst, itemRequestEntitySecond);
    }

    @Test
    void canFindAllByUserIdNot() {
        Pageable pageable = new OffsetPageable(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        List<ItemRequestEntity> itemRequestEntityList = itemRequestRepository.findAllByUserIdNot(itemRequestor.getId(), pageable);
        Assertions.assertThat(itemRequestEntityList).isNotNull();
        Assertions.assertThat(itemRequestEntityList).containsExactlyInAnyOrder(itemRequestEntityThird);
    }

}