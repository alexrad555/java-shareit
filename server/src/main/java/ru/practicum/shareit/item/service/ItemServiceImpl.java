package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.OffsetPageable;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.controller.dto.CommentCreateRequest;
import ru.practicum.shareit.item.controller.dto.ItemCreateRequest;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRequestService itemRequestService;

    @Override
    public Item create(ItemCreateRequest itemCreateRequest, Long userId) {

        User user = userService.findById(userId);
        Item item = itemMapper.toItem(itemCreateRequest);
        item.setOwner(user);
        item.setRequest(itemRequestService.findOptionalById(itemCreateRequest.getRequestId()).orElse(null));
        ItemEntity itemEntity = itemMapper.toEntity(item);
        itemRepository.save(itemEntity);
        return getItem(itemMapper.toItem(itemEntity), userId);
    }

    @Override
    public Item findById(Long itemId, Long userId) {
        return itemRepository.findById(itemId)
                .map(itemMapper::toItem)
                .map(item -> getItem(item, userId))
                .orElseThrow(
                        () -> new DataNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
    }

    @Override
    public List<Item> search(String text, Long userId, int from, int size) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        Pageable pageable = new OffsetPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemRepository.findAllByNameOrDescription(text, pageable).stream()
                .map(itemMapper::toItem)
                .map(item -> getItem(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item itemDto, Long userId, Long itemId) {
        if (itemId == null) {
            throw new ValidationException("не приходить id");
        }
        Item item = findById(itemId, userId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("нет прав на изменение");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        ItemEntity itemEntity = itemMapper.toEntity(item);
        itemRepository.save(itemEntity);
        return getItem(itemMapper.toItem(itemEntity), userId);
    }

    @Override
    public List<Item> findAllByOwnerId(Long userId, int from, int size) {
        Pageable pageable = new OffsetPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemRepository.findAllByOwnerId(userId, pageable).stream()
                .map(itemMapper::toItem)
                .map(item -> getItem(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public Comment createComment(Long itemId, CommentCreateRequest commentCreateRequest, Long userId) {
        Item item = findById(itemId, userId);
        User user = userService.findById(userId);
        bookingRepository.findAllByBookerIdAndItemId(userId, itemId).stream()
                .map(bookingMapper::toBooking)
                .filter(Booking::isFinished)
                .findAny()
                .orElseThrow(() -> new ValidationException("Не было бронирования"));
        Comment comment = new Comment();
        comment.setText(commentCreateRequest.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        CommentEntity commentEntity = commentMapper.toEntity(comment);
        commentRepository.save(commentEntity);
        return commentMapper.toComment(commentEntity);

    }

    private Item getItem(Item item, Long userId) {
        item.setComments(commentMapper.toComment(commentRepository.findCommentByItemId(item.getId())));
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime currentTime = LocalDateTime.now();
            BookingEntity last = bookingRepository
                    .findFirstByItemIdAndStatusAndStartDateBeforeOrderByStartDateDesc(
                            item.getId(),
                            BookingStatus.APPROVED,
                            currentTime
                    )
                    .orElse(null);
            item.setLastBooking(bookingMapper.toBooking(last));
            BookingEntity next = bookingRepository
                    .findFirstByItemIdAndStatusAndStartDateAfterOrderByStartDate(
                            item.getId(),
                            BookingStatus.APPROVED,
                            currentTime
                    )
                    .orElse(null);
            item.setNextBooking(bookingMapper.toBooking(next));
        }
        return item;
    }
}
