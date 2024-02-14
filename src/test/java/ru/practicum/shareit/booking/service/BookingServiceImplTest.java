package ru.practicum.shareit.booking.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.mapper.LinkedBookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.CommentResponseMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ItemMapperImpl.class,
                CommentMapperImpl.class,
                BookingMapperImpl.class,
                CommentResponseMapperImpl.class,
                LinkedBookingMapperImpl.class,
                BookingServiceImpl.class
        }
)
class BookingServiceImplTest {

    @MockBean
    UserService userService;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingRepository bookingRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingMapper bookingMapper;

    static User itemOwner;

    static User bookerUser;

    static ItemRequest itemRequestBook;


    @BeforeAll
    static void beforeAll() {
        itemOwner = new User(1L, "Tod", "user@user.com");
        bookerUser = new User(2L, "Bob", "user2@user.com");
        itemRequestBook = new ItemRequest(1L, "Create Book", LocalDateTime.now(), null);
    }

    @Test
    void canCreateBooking() {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest();
        bookingCreateRequest.setItemId(1L);
        bookingCreateRequest.setStart(LocalDateTime.now().plusMinutes(5));
        bookingCreateRequest.setEnd(LocalDateTime.now().plusMinutes(10));

        Item itemBook = new Item(1L, "Book", "Read book", true,
                itemOwner, itemRequestBook, Collections.EMPTY_LIST, null, null);

        when(userService.findById(anyLong())).thenReturn(bookerUser);
        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemBook);

        Booking booking = bookingService.create(bookingCreateRequest, bookerUser.getId());
        Assertions.assertThat(booking).isNotNull();
        Assertions.assertThat(bookingCreateRequest.getItemId()).isEqualTo(booking.getItem().getId());
        Assertions.assertThat(bookingCreateRequest.getStart()).isEqualTo(booking.getStartDate());
        Assertions.assertThat(bookingCreateRequest.getEnd()).isEqualTo(booking.getEndDate());
    }

    @Test
    void willThrowWhenStartAfterEnd() {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest();
        bookingCreateRequest.setItemId(1L);
        bookingCreateRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingCreateRequest.setEnd(LocalDateTime.now().plusMinutes(5));
        Assertions.assertThatThrownBy(
                        () -> bookingService.create(bookingCreateRequest, bookerUser.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void willThrowWhenAvailableIsFalse() {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest();
        bookingCreateRequest.setItemId(1L);
        bookingCreateRequest.setStart(LocalDateTime.now().plusMinutes(5));
        bookingCreateRequest.setEnd(LocalDateTime.now().plusMinutes(10));

        Item itemBook = new Item(1L, "Book", "Read book", true,
                itemOwner, itemRequestBook, Collections.EMPTY_LIST, null, null);

        when(userService.findById(anyLong())).thenReturn(bookerUser);
        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemBook);

        itemBook.setAvailable(false);
        Assertions.assertThatThrownBy(
                        () -> bookingService.create(bookingCreateRequest, bookerUser.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void willThrowWhenBookingIsOwner() {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest();
        bookingCreateRequest.setItemId(1L);
        bookingCreateRequest.setStart(LocalDateTime.now().plusMinutes(5));
        bookingCreateRequest.setEnd(LocalDateTime.now().plusMinutes(10));

        Item itemBook = new Item(1L, "Book", "Read book", true,
                itemOwner, itemRequestBook, Collections.EMPTY_LIST, null, null);

        when(userService.findById(anyLong())).thenReturn(itemOwner);
        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemBook);

        Assertions.assertThatThrownBy(
                        () -> bookingService.create(bookingCreateRequest, itemOwner.getId()))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void canFindIdAndUserId() {
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, null);
        BookingEntity bookingEntity = new BookingEntity(1L, LocalDateTime.now().minusSeconds(30),
                LocalDateTime.now(), itemEntity, bookerUser, BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));
        Booking booking = bookingService.findByIdAndUserId(bookingEntity.getId(), bookerUser.getId());

         Assertions.assertThat(booking).isEqualTo(bookingMapper.toBooking(bookingEntity));
    }

    @Test
    void willThrowWhenUserIsOwner() {
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, null);
        BookingEntity bookingEntity = new BookingEntity(1L, LocalDateTime.now().minusSeconds(30),
                LocalDateTime.now(), itemEntity, itemOwner, BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));

        Assertions.assertThatThrownBy(
                        () -> bookingService.findByIdAndUserId(bookingEntity.getId(), 5L))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void canUpdateBooking() {
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, null);
        BookingEntity bookingEntity = new BookingEntity(1L, LocalDateTime.now().minusSeconds(30),
                LocalDateTime.now(), itemEntity, bookerUser, BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));
        Booking booking = bookingService.update(bookingEntity.getId(), itemOwner.getId(), true);

        Assertions.assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);

    }

    @Test
    void willThrowWhenUpdateStatusNotWaiting() {
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, null);
        BookingEntity bookingEntity = new BookingEntity(1L, LocalDateTime.now().minusSeconds(30),
                LocalDateTime.now(), itemEntity, bookerUser, BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));

        Assertions.assertThatThrownBy(
                        () -> bookingService.update(bookingEntity.getId(), itemOwner.getId(), true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void willThrowWhenUpdateBookingNotFound() {
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, null);
        BookingEntity bookingEntity = new BookingEntity(1L, LocalDateTime.now().minusSeconds(30),
                LocalDateTime.now(), itemEntity, bookerUser, BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));

        Assertions.assertThatThrownBy(
                        () -> bookingService.update(bookingEntity.getId(), bookerUser.getId(), true))
                .isInstanceOf(DataNotFoundException.class);

    }

    @Test
    void willThrowWhenItemNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(
                        () -> bookingService.findByIdAndUserId(99L, itemOwner.getId()))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void canFindAllByOwner() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStatus(BookingStatus.APPROVED);
        bookingEntity.setStartDate(LocalDateTime.now().minusSeconds(1));
        bookingEntity.setEndDate(LocalDateTime.now().plusMinutes(1));

        when(userService.findById(anyLong())).thenReturn(itemOwner);
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(List.of(bookingEntity));

        List<Booking> bookingList = bookingService.findAllByOwner(itemOwner.getId(), BookingState.ALL, 0, 20);
        Assertions.assertThat(bookingList).isNotNull();
        Assertions.assertThat(bookingList.size()).isEqualTo(1);
        Assertions.assertThat(bookingList).extracting(Booking::getStatus).containsOnly(BookingStatus.APPROVED);
    }

    @Test
    void canFindAllByBooker() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStatus(BookingStatus.APPROVED);
        bookingEntity.setStartDate(LocalDateTime.now().minusSeconds(1));
        bookingEntity.setEndDate(LocalDateTime.now().plusMinutes(1));

        when(userService.findById(anyLong())).thenReturn(bookerUser);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(bookingEntity));

        List<Booking> bookingList = bookingService.findAllByBooker(bookerUser.getId(), BookingState.ALL, 0, 20);
        Assertions.assertThat(bookingList).isNotNull();
        Assertions.assertThat(bookingList.size()).isEqualTo(1);
        Assertions.assertThat(bookingList).extracting(Booking::getStatus).containsOnly(BookingStatus.APPROVED);
    }

    @Test
    void canFindAllByOwnerStateIsPast() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStatus(BookingStatus.APPROVED);
        bookingEntity.setStartDate(LocalDateTime.now().minusSeconds(12));
        bookingEntity.setEndDate(LocalDateTime.now().minusSeconds(1));

        when(userService.findById(anyLong())).thenReturn(bookerUser);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(bookingEntity));

        List<Booking> bookingList = bookingService.findAllByBooker(bookerUser.getId(), BookingState.PAST, 0, 20);
        Assertions.assertThat(bookingList).isNotNull();
        Assertions.assertThat(bookingList.size()).isEqualTo(1);
        Assertions.assertThat(bookingList).extracting(Booking::getStatus).containsOnly(BookingStatus.APPROVED);
    }

    @Test
    void canFindAllByOwnerStateIsFuture() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStatus(BookingStatus.APPROVED);
        bookingEntity.setStartDate(LocalDateTime.now().plusMinutes(12));
        bookingEntity.setEndDate(LocalDateTime.now().plusMinutes(20));

        when(userService.findById(anyLong())).thenReturn(bookerUser);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(bookingEntity));

        List<Booking> bookingList = bookingService.findAllByBooker(bookerUser.getId(), BookingState.FUTURE, 0, 20);
        Assertions.assertThat(bookingList).isNotNull();
        Assertions.assertThat(bookingList.size()).isEqualTo(1);
        Assertions.assertThat(bookingList).extracting(Booking::getStatus).containsOnly(BookingStatus.APPROVED);
    }

    @Test
    void canFindAllByOwnerStateIsCurrent() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStatus(BookingStatus.APPROVED);
        bookingEntity.setStartDate(LocalDateTime.now().minusMinutes(12));
        bookingEntity.setEndDate(LocalDateTime.now().plusMinutes(20));

        when(userService.findById(anyLong())).thenReturn(bookerUser);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(bookingEntity));

        List<Booking> bookingList = bookingService.findAllByBooker(bookerUser.getId(), BookingState.CURRENT, 0, 20);
        Assertions.assertThat(bookingList).isNotNull();
        Assertions.assertThat(bookingList.size()).isEqualTo(1);
        Assertions.assertThat(bookingList).extracting(Booking::getStatus).containsOnly(BookingStatus.APPROVED);
    }

    @Test
    void canFindAllByOwnerStateIsWaiting() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStatus(BookingStatus.WAITING);
        bookingEntity.setStartDate(LocalDateTime.now().plusMinutes(12));
        bookingEntity.setEndDate(LocalDateTime.now().plusMinutes(20));

        when(userService.findById(anyLong())).thenReturn(bookerUser);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(bookingEntity));

        List<Booking> bookingList = bookingService.findAllByBooker(bookerUser.getId(), BookingState.WAITING, 0, 20);
        Assertions.assertThat(bookingList).isNotNull();
        Assertions.assertThat(bookingList.size()).isEqualTo(1);
        Assertions.assertThat(bookingList).extracting(Booking::getStatus).containsOnly(BookingStatus.WAITING);
    }
}