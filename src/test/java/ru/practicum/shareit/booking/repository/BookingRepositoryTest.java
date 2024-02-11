package ru.practicum.shareit.booking.repository;

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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    static User itemOwner;

    static User booker;

    ItemEntity itemEntityFirst;

    ItemEntity itemEntitySecond;

    ItemEntity itemEntityThird;

    BookingEntity bookingEntityFirst;

    BookingEntity bookingEntitySecond;

    ItemRequestEntity itemRequestEntity;

    @BeforeAll
    void setup() {
        itemOwner = new User(1L, "Tod", "user@user.com");
        booker = new User(2L, "Bob", "user2@user.com");
        itemRequestEntity = new ItemRequestEntity(1L, "Create Book", booker, LocalDateTime.now());
        itemEntityFirst = new ItemEntity(1L, "Book", "Read book",
                true, itemOwner, itemRequestEntity);
        itemEntitySecond = new ItemEntity(2L, "Pen", "pen desc",
                true, booker, itemRequestEntity);
        itemEntityThird = new ItemEntity(3L, "Disc", "play disc",
                true, itemOwner, itemRequestEntity);
        bookingEntityFirst = new BookingEntity(1L, LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10), itemEntityFirst, booker, BookingStatus.WAITING);
        bookingEntitySecond = new BookingEntity(2L, LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10), itemEntitySecond, booker, BookingStatus.WAITING);
        userRepository.save(itemOwner);
        userRepository.save(booker);
        itemRequestRepository.save(itemRequestEntity);
        itemRepository.save(itemEntityFirst);
        itemRepository.save(itemEntitySecond);
        itemRepository.save(itemEntityThird);
        bookingRepository.save(bookingEntityFirst);
        bookingRepository.save(bookingEntitySecond);
    }
    @Test
    void canFindAllByBookerId() {
        Pageable pageable = new OffsetPageable(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        List<BookingEntity> bookingEntityList = bookingRepository.findAllByBookerId(booker.getId(), pageable);
        Assertions.assertThat(bookingEntityList).isNotNull();
        Assertions.assertThat(bookingEntityList).containsExactlyInAnyOrder(bookingEntityFirst, bookingEntitySecond);
    }
}