package ru.practicum.shareit.booking;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {

    Long id;
    LocalDate start;
    LocalDate end;
    Long item;
    Long booker;
    BookingStatus status;
}
