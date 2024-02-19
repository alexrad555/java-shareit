package ru.practicum.shareit.booking.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.exception.ValidationException;

@Component
public class StateConverter implements Converter<String, BookingState> {

    @Override
    public BookingState convert(String source) {
        try {
            return BookingState.valueOf(source);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + source);
        }
    }
}
