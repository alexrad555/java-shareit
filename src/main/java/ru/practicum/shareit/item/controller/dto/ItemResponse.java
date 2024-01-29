package ru.practicum.shareit.item.controller.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.controller.dto.LinkedBookingResponse;

import java.util.List;

@Getter
@Setter
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentResponse> comments;
    private LinkedBookingResponse lastBooking;
    private LinkedBookingResponse nextBooking;
}
