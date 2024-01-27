package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface BookingMapper {

    @Mapping(target = "start", source = "startDate")
    @Mapping(target = "end", source = "endDate")
    BookingResponse toResponse(Booking booking);

    Booking toBooking(BookingEntity bookingEntity);

    BookingEntity toEntity(Booking booking);

    List<BookingResponse> toResponse(List<Booking> bookingList);

    List<Booking> toBooking(List<BookingEntity> bookingEntitiesList);

}
