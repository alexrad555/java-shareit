package ru.practicum.shareit.booking.model;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Getter
@Setter
public class Booking {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Item item;
    private User booker;
    private BookingStatus status;

    public boolean isFinished() {
        return getStatus() == BookingStatus.APPROVED && getEndDate().isBefore(LocalDateTime.now());
    }
}
