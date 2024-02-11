package ru.practicum.shareit.booking.controller.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.user.controller.dto.UserResponse;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@EqualsAndHashCode
public class BookingResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemResponse item;
    private UserResponse booker;
    private BookingStatus status;
}
