package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingCreateRequest bookingDto, Long userId);

    Booking findByIdAndUserId(Long bookingId, Long userId);

    Booking update(Long bookingId, Long userId, boolean approved);

    List<Booking> findAllByBooker(Long userId, BookingState state, int from, int size);

    List<Booking> findAllByOwner(Long userId, BookingState state, int from, int size);
}
