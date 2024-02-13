package ru.practicum.shareit.item.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.mapper.LinkedBookingMapperImpl;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.controller.dto.CommentCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
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
                ItemServiceImpl.class
        }
)
class ItemServiceImplTest {
    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserService userService;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    ItemService itemService;

    static User itemOwner;

    static User itemRequestor;

    @BeforeAll
    static void beforeAll() {
        itemOwner = new User(1L, "Tod", "user@user.com");
        itemRequestor = new User(2L, "Bob", "user2@user.com");
    }


    @Test
    void canCreateItem() {
        ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Book", "booking", true, 1L);
        ItemRequest itemRequest = new ItemRequest(1L, "Create Book", LocalDateTime.now(), null);
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity(itemRequest.getId(), itemRequest.getDescription(), itemRequestor, itemRequest.getCreated());
        ItemEntity itemEntity = new ItemEntity(1L, itemCreateRequest.getName(), itemCreateRequest.getDescription(),
                itemCreateRequest.getAvailable(), itemOwner, itemRequestEntity);

        when(userService.findById(anyLong())).thenReturn(itemOwner);
        when(itemRequestService.findOptionalById(any())).thenReturn(Optional.of(itemRequest));

        Item item = itemService.create(itemCreateRequest, itemOwner.getId());
        Assertions.assertThat(item).isNotNull();
        Assertions.assertThat(itemCreateRequest.getName()).isEqualTo(item.getName());
        Assertions.assertThat(itemCreateRequest.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(itemCreateRequest.getAvailable()).isEqualTo(item.getAvailable());
        Assertions.assertThat(itemCreateRequest.getRequestId()).isEqualTo(item.getRequest().getId());
    }

    @Test
    void willThrowWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(
                () -> itemService.findById(99L, itemOwner.getId()))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void canFindById() {
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, null);
        CommentEntity commentEntity = new CommentEntity(1L, "Good",
                itemEntity, itemRequestor, LocalDateTime.now());
        BookingEntity bookingEntity = new BookingEntity();
        Item origionItem = itemMapper.toItem(itemEntity);
        origionItem.setComments(commentMapper.toComment(List.of(commentEntity)));
        when(commentRepository.findCommentByItemId(anyLong())).thenReturn(List.of(commentEntity));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemEntity));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartDateBeforeOrderByStartDateDesc(anyLong(),any(),any()))
                .thenReturn(Optional.of(bookingEntity));
        Item itemRes = itemService.findById(itemEntity.getId(), itemRequestor.getId());
        Assertions.assertThat(itemRes).isEqualTo(origionItem);
    }

    @Test
    void canFindByIdForOwner() {
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, null);
        CommentEntity commentEntity = new CommentEntity(1L, "Good",
                itemEntity, itemRequestor, LocalDateTime.now());
        BookingEntity bookingEntityLast = new BookingEntity(1L, LocalDateTime.now().minusSeconds(30),
                LocalDateTime.now(), itemEntity, itemRequestor, BookingStatus.APPROVED);
        BookingEntity bookingEntityNext = new BookingEntity(2L, LocalDateTime.now().minusSeconds(10),
                LocalDateTime.now(), itemEntity, itemRequestor, BookingStatus.APPROVED);
        Item origionItem = itemMapper.toItem(itemEntity);
        origionItem.setLastBooking(bookingMapper.toBooking(bookingEntityLast));
        origionItem.setNextBooking(bookingMapper.toBooking(bookingEntityNext));
        origionItem.setComments(commentMapper.toComment(List.of(commentEntity)));
        when(commentRepository.findCommentByItemId(anyLong())).thenReturn(List.of(commentEntity));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemEntity));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartDateBeforeOrderByStartDateDesc(anyLong(),any(),any()))
                .thenReturn(Optional.of(bookingEntityLast));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartDateAfterOrderByStartDate(anyLong(),any(),any()))
                .thenReturn(Optional.of(bookingEntityNext));
        Item itemRes = itemService.findById(itemEntity.getId(), itemOwner.getId());
        Assertions.assertThat(itemRes).isEqualTo(origionItem);
    }

    @Test
    void canFindByNameOrDescription() {
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null);
        when(itemRepository.findAllByNameOrDescription(any(), any())).thenReturn(List.of(itemMapper.toEntity(itemFirst)));
        List<Item> itemList = itemService.search("book", itemRequestor.getId(), 0, 20);
        Assertions.assertThat(itemList).containsOnly(itemFirst);
    }

    @Test
    void willReturnEmptyListWhenTextIsBlank() {
        List<Item> itemList = itemService.search("   ", itemRequestor.getId(), 0, 20);
        Assertions.assertThat(itemList.size()).isEqualTo(0);
        itemList = itemService.search(null, itemRequestor.getId(), 0, 20);
        Assertions.assertThat(itemList.size()).isEqualTo(0);
    }

    @Test
    void canFindAllByOwnerId() {
        Item itemFirst = new Item(1L, "Book", "Read book", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null
        );
        Item itemSecond = new Item(2L, "Book", "Read book", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null
        );
        List<Item> listItem = List.of(itemFirst, itemSecond);
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(itemMapper.toEntity(listItem));
        List<Item> itemListRes = itemService.findAllByOwnerId(itemOwner.getId(), 0, 20);
        Assertions.assertThat(itemListRes).containsExactlyInAnyOrderElementsOf(listItem);
    }



    @Test
    void canCreateComment() {
        Item item = new Item(1L, "Book", "Read book", true,
                itemOwner, null, null, null, null);
        ItemEntity itemEntity = itemMapper.toEntity(item);
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setEndDate(LocalDateTime.now().minusSeconds(5));
        bookingEntity.setStatus(BookingStatus.APPROVED);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemEntity));
        when(userService.findById(anyLong())).thenReturn(itemRequestor);
        when(bookingRepository.findAllByBookerIdAndItemId(anyLong(),anyLong()))
                .thenReturn(List.of(bookingEntity));
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setText("123");
        Comment commentRes = itemService.createComment(item.getId(), commentCreateRequest, itemRequestor.getId());
        Assertions.assertThat(commentRes.getItem()).isEqualTo(item);
        Assertions.assertThat(commentRes.getAuthor()).isEqualTo(itemRequestor);
        Assertions.assertThat(commentRes.getText()).isEqualTo(commentCreateRequest.getText());
    }

    @Test
    void canUpdateName() {
        ItemRequest itemRequest = new ItemRequest(1L, "Create Book", LocalDateTime.now(), null);
        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest();
        itemUpdateRequest.setId(1L);
        itemUpdateRequest.setName("Update Name");
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity(itemRequest.getId(), itemRequest.getDescription(), itemRequestor, itemRequest.getCreated());
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, itemRequestEntity);
        Item item = itemMapper.toItem(itemEntity);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemEntity));
        Item itemRes = itemService.update(itemMapper.toItemUpdate(itemUpdateRequest), itemOwner.getId(), itemUpdateRequest.getId());
        Assertions.assertThat(itemRes).isNotNull();
        Assertions.assertThat(itemRes.getName()).isEqualTo(itemUpdateRequest.getName());
        Assertions.assertThat(itemRes.getId()).isEqualTo(item.getId());
        Assertions.assertThat(itemRes.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(itemRes.getAvailable()).isEqualTo(item.getAvailable());
        Assertions.assertThat(itemRes.getRequest()).isEqualTo(itemRequest);
        Assertions.assertThat(itemRes.getOwner()).isEqualTo(itemOwner);
    }

    @Test
    void canUpdateDescription() {
        ItemRequest itemRequest = new ItemRequest(1L, "Create Book", LocalDateTime.now(), null);
        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest();
        itemUpdateRequest.setId(1L);
        itemUpdateRequest.setDescription("Update Description");
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity(itemRequest.getId(), itemRequest.getDescription(), itemRequestor, itemRequest.getCreated());
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, itemRequestEntity);
        Item item = itemMapper.toItem(itemEntity);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemEntity));
        Item itemRes = itemService.update(itemMapper.toItemUpdate(itemUpdateRequest), itemOwner.getId(), itemUpdateRequest.getId());
        Assertions.assertThat(itemRes).isNotNull();
        Assertions.assertThat(itemRes.getDescription()).isEqualTo(itemUpdateRequest.getDescription());
        Assertions.assertThat(itemRes.getId()).isEqualTo(item.getId());
        Assertions.assertThat(itemRes.getName()).isEqualTo(item.getName());
        Assertions.assertThat(itemRes.getAvailable()).isEqualTo(item.getAvailable());
        Assertions.assertThat(itemRes.getRequest()).isEqualTo(itemRequest);
        Assertions.assertThat(itemRes.getOwner()).isEqualTo(itemOwner);
    }

    @Test
    void canUpdateAvailable() {
        ItemRequest itemRequest = new ItemRequest(1L, "Create Book", LocalDateTime.now(), null);
        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest();
        itemUpdateRequest.setId(1L);
        itemUpdateRequest.setAvailable(false);
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity(itemRequest.getId(), itemRequest.getDescription(), itemRequestor, itemRequest.getCreated());
        ItemEntity itemEntity = new ItemEntity(1L, "Book",
                "Read book", true, itemOwner, itemRequestEntity);
        Item item = itemMapper.toItem(itemEntity);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemEntity));
        Item itemRes = itemService.update(itemMapper.toItemUpdate(itemUpdateRequest), itemOwner.getId(), itemUpdateRequest.getId());
        Assertions.assertThat(itemRes).isNotNull();
        Assertions.assertThat(itemRes.getAvailable()).isEqualTo(itemUpdateRequest.getAvailable());
        Assertions.assertThat(itemRes.getId()).isEqualTo(item.getId());
        Assertions.assertThat(itemRes.getName()).isEqualTo(item.getName());
        Assertions.assertThat(itemRes.getDescription()).isEqualTo(item.getDescription());
        Assertions.assertThat(itemRes.getRequest()).isEqualTo(itemRequest);
        Assertions.assertThat(itemRes.getOwner()).isEqualTo(itemOwner);
    }

    @Test
    void willThrowValidationExceptionWhenItemIdIsNull() {
        Assertions.assertThatThrownBy(
                        () -> itemService.update(null, null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void willThrowDataNotFoundExceptionWhenUserNotOwner() {
        Item item = new Item(1L, "Book", "Read book", true,
                itemOwner, null, Collections.EMPTY_LIST, null, null
        );
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemMapper.toEntity(item)));
        Assertions.assertThatThrownBy(
                        () -> itemService.update(null, itemRequestor.getId(), item.getId()))
                .isInstanceOf(DataNotFoundException.class);
    }

}