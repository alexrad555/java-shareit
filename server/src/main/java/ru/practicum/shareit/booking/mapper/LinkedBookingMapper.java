package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.controller.dto.LinkedBookingResponse;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface LinkedBookingMapper {
    @Mapping(target = "start", source = "startDate")
    @Mapping(target = "end", source = "endDate")
    @Mapping(target = "bookerId", source = "booker.id")
    LinkedBookingResponse toLinkedBookingResponse(Booking booking);
}
