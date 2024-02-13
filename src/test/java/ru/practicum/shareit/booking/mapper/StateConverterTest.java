package ru.practicum.shareit.booking.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.exception.ValidationException;

class StateConverterTest {


    @Test
    void convert() {
        StateConverter stateConverter = new StateConverter();
        for (BookingState value : BookingState.values()) {
            String val = value.name();
            BookingState state = stateConverter.convert(val);
            Assertions.assertThat(state).isEqualTo(value);
        }
        Assertions.assertThatThrownBy(() -> stateConverter.convert("-"))
                .isInstanceOf(ValidationException.class);
    }

}