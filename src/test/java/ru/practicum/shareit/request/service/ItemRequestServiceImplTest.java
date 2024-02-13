package ru.practicum.shareit.request.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.mapper.LinkedBookingMapperImpl;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.mapper.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.dto.ItemRequestCreate;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ItemMapperImpl.class,
                ItemRequestMapperImpl.class,
                CommentMapperImpl.class,
                BookingMapperImpl.class,
                CommentResponseMapperImpl.class,
                LinkedBookingMapperImpl.class,
                ItemRequestServiceImpl.class
        }
)
class ItemRequestServiceImplTest {

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserService userService;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @Autowired
    ItemRequestMapper itemRequestMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemRequestService itemRequestService;

    static User userRequestor;

    static User userSecond;

    @BeforeAll
    static void beforeAll() {
        userRequestor = new User(1L, "Tod", "user@user.com");
        userSecond = new User(2L, "Bob", "user2@user.com");
    }

    @Test
    void canCreateItemRequest() {
        ItemRequestCreate itemRequestCreate = new ItemRequestCreate();
        itemRequestCreate.setDescription("Need book");
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity();
        itemRequestEntity.setId(1L);
        itemRequestEntity.setCreated(LocalDateTime.now());
        itemRequestEntity.setDescription(itemRequestCreate.getDescription());
        itemRequestEntity.setRequestor(userRequestor);

        when(userService.findById(anyLong())).thenReturn(userRequestor);
        when(itemRequestRepository.save(any())).thenReturn(itemRequestEntity);

        ItemRequest itemRequestRes = itemRequestService.create(userRequestor.getId(), itemRequestCreate);

        Assertions.assertThat(itemRequestRes).isNotNull();
        Assertions.assertThat(itemRequestRes.getId()).isEqualTo(itemRequestEntity.getId());
        Assertions.assertThat(itemRequestRes.getDescription()).isEqualTo(itemRequestEntity.getDescription());
        Assertions.assertThat(itemRequestRes.getCreated()).isEqualTo(itemRequestEntity.getCreated());
    }

    @Test
    void canFindAllByOwnUserId() {
        ItemRequestEntity itemRequestEntityFirst = new ItemRequestEntity(1L, "Need book", userRequestor, LocalDateTime.now());
        ItemRequestEntity itemRequestEntityLast = new ItemRequestEntity(2L, "Need pen", userRequestor, LocalDateTime.now());
        List<ItemRequestEntity> itemRequestEntityList = List.of(itemRequestEntityFirst, itemRequestEntityLast);

        when(userService.findById(anyLong())).thenReturn(userRequestor);
        when(itemRequestRepository.findAllByRequestorId(anyLong(),any())).thenReturn(itemRequestEntityList);

        List<ItemRequest> itemRequestList = itemRequestService.findAllByOwnUserId(userRequestor.getId(), 0, 20);
        Assertions.assertThat(itemRequestList.size()).isEqualTo(itemRequestEntityList.size());
    }

    @Test
    void canFindAllByOtherUserId() {
        ItemRequestEntity itemRequestEntityFirst = new ItemRequestEntity(1L, "Need book", userSecond, LocalDateTime.now());
        ItemRequestEntity itemRequestEntityLast = new ItemRequestEntity(2L, "Need pen", userSecond, LocalDateTime.now());
        List<ItemRequestEntity> itemRequestEntityList = List.of(itemRequestEntityFirst, itemRequestEntityLast);

        when(userService.findById(anyLong())).thenReturn(userSecond);
        when(itemRequestRepository.findAllByUserIdNot(anyLong(),any())).thenReturn(itemRequestEntityList);

        List<ItemRequest> itemRequestList = itemRequestService.findAllByOtherUserId(userSecond.getId(), 0, 20);
        Assertions.assertThat(itemRequestList.size()).isEqualTo(itemRequestEntityList.size());
    }

    @Test
    void canFindById() {
        ItemRequestEntity itemRequestEntityFirst = new ItemRequestEntity(1L, "Need book", userSecond, LocalDateTime.now());
        when(userService.findById(anyLong())).thenReturn(userRequestor);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequestEntityFirst));

        ItemRequest itemRequest = itemRequestService.findById(userRequestor.getId(), itemRequestEntityFirst.getId());
        Assertions.assertThat(itemRequest.getId()).isEqualTo(itemRequestEntityFirst.getId());
        Assertions.assertThat(itemRequest.getDescription()).isEqualTo(itemRequestEntityFirst.getDescription());
        Assertions.assertThat(itemRequest.getCreated()).isEqualTo(itemRequestEntityFirst.getCreated());
    }

    @Test
    void willThrowWhenItemRequestNotFound() {
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity(1L, "Need book", userSecond, LocalDateTime.now());
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(
                        () -> itemRequestService.findById(99L, itemRequestEntity.getId()))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void willReturnEmptyWhenRequestIdIsNull() {
        Optional<ItemRequest> itemRequest = itemRequestService.findOptionalById(null);
        Assertions.assertThat(itemRequest.isEmpty()).isTrue();
    }

}