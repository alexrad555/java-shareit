package ru.practicum.shareit.item.repository;

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
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    static User itemOwner;

    static User itemRequestor;

    ItemEntity itemEntityFirst;

    ItemEntity itemEntitySecond;

    ItemEntity itemEntityThird;

    ItemRequestEntity itemRequestEntity;

    @BeforeAll
    void setup() {
        itemOwner = new User(1L, "Tod", "user@user.com");
        itemRequestor = new User(2L, "Bob", "user2@user.com");
        itemRequestEntity = new ItemRequestEntity(1L, "Create Book", itemRequestor, LocalDateTime.now());
        itemEntityFirst = new ItemEntity(1L, "Book", "Read book",
                true, itemOwner, itemRequestEntity);
        itemEntitySecond = new ItemEntity(2L, "Pen", "pen desc",
                true, itemRequestor, itemRequestEntity);
        itemEntityThird = new ItemEntity(3L, "Disc", "play disc",
                true, itemOwner, itemRequestEntity);
        userRepository.save(itemOwner);
        userRepository.save(itemRequestor);
        itemRequestRepository.save(itemRequestEntity);
        itemRepository.save(itemEntityFirst);
        itemRepository.save(itemEntitySecond);
        itemRepository.save(itemEntityThird);
    }

    @Test
    void canFindAllByOwnerId() {
        Pageable pageable = new OffsetPageable(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        List<ItemEntity> itemEntityList = itemRepository.findAllByOwnerId(itemOwner.getId(), pageable);
        Assertions.assertThat(itemEntityList).isNotNull();
        Assertions.assertThat(itemEntityList).containsExactlyInAnyOrder(itemEntityFirst, itemEntityThird);
    }

    @Test
    void canFindAllByNameOrDescription() {
        Pageable pageable = new OffsetPageable(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        List<ItemEntity> itemEntityList = itemRepository.findAllByNameOrDescription("book", pageable);
        Assertions.assertThat(itemEntityList).isNotNull();
        Assertions.assertThat(itemEntityList).containsExactlyInAnyOrder(itemEntityFirst);
    }

    @Test
    void canFindAllByRequestIdIn() {

    }

}