package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.OffsetPageable;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Override
    public Booking create(BookingCreateRequest bookingDto, Long userId) {
        Objects.requireNonNull(bookingDto);
        Objects.requireNonNull(userId);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("некорректные даты бронирования");
        }
        User user = userService.findById(userId);
        Item item = itemService.findById(bookingDto.getItemId(), userId);
        if (!item.getAvailable()) {
            throw new ValidationException("некорректные данные бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Нельзя забронировать свой предмет");
        }
        Booking booking = new Booking();
        booking.setStartDate(bookingDto.getStart());
        booking.setEndDate(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(WAITING);
        BookingEntity bookingEntity = bookingMapper.toEntity(booking);
        bookingRepository.save(bookingEntity);
        return bookingMapper.toBooking(bookingEntity);
    }

    @Override
    public Booking findByIdAndUserId(Long bookingId, Long userId) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElseThrow(
                () -> new DataNotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
        Booking booking = bookingMapper.toBooking(bookingEntity);
        if (!userIsOwner(userId, booking)
                && !userIsBooker(userId, booking)) {
            throw new DataNotFoundException(String.format("Бронирование с id %d не найдено", bookingId));
        }
        return booking;
    }

    @Override
    public Booking update(Long bookingId, Long userId, boolean approved) {
        Booking booking = findByIdAndUserId(bookingId, userId);
        if (!userIsOwner(userId, booking)) {
            throw new DataNotFoundException(String.format("Бронирование с id %d не найдено", bookingId));
        }
        if (booking.getStatus() != WAITING) {
            throw new ValidationException("Менять статус бронирования запрещено");
        }
        booking.setStatus(approved ? APPROVED : REJECTED);
        BookingEntity bookingEntity = bookingMapper.toEntity(booking);
        bookingRepository.save(bookingEntity);
        return bookingMapper.toBooking(bookingEntity);
    }

    @Override
    public List<Booking> findAllByBooker(Long userId, BookingState state, int from, int size) {
        Pageable pageable = new OffsetPageable(from, size, Sort.by(Sort.Direction.DESC, "startDate"));
        List<Booking> bookingList = bookingMapper.toBooking(bookingRepository.findAllByBookerId(userId, pageable));
        return bookingList.stream()
                .filter(booking -> checkBookingState(booking, state))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findAllByOwner(Long userId, BookingState state, int from, int size) {
        User user = userService.findById(userId);
        Pageable pageable = new OffsetPageable(from, size, Sort.by(Sort.Direction.DESC, "startDate"));
        List<Booking> bookingList = bookingMapper.toBooking(bookingRepository.findAllByItemOwnerId(userId, pageable));
        return bookingList.stream()
                .filter(booking -> checkBookingState(booking, state))
                .collect(Collectors.toList());
    }

    private boolean userIsOwner(Long userId, Booking booking) {
        return booking.getItem().getOwner().getId().equals(userId);
    }

    private boolean userIsBooker(Long userId, Booking booking) {
        return booking.getBooker().getId().equals(userId);
    }

    private boolean checkBookingState(Booking booking, BookingState state) {
        switch (state) {
                case PAST:
                return booking.getEndDate().isBefore(LocalDateTime.now());
            case FUTURE:
                return booking.getStartDate().isAfter(LocalDateTime.now());
            case CURRENT:
                return booking.getStartDate().isBefore(LocalDateTime.now())
                        && booking.getEndDate().isAfter(LocalDateTime.now());
            case WAITING:
            case REJECTED:
                return booking.getStatus().name().equals(state.name());
            default:
                return true;
        }
    }
}
