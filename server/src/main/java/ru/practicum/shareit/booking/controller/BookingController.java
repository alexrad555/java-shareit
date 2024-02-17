package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingMapper bookingMapper;
    private final BookingService bookingService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponse create(@RequestBody BookingCreateRequest bookingDto,
                                  @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return bookingMapper.toResponse(bookingService.create(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse update(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                  @RequestParam() boolean approved,
                                  @PathVariable Long bookingId) {
        return bookingMapper.toResponse(bookingService.update(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                      @PathVariable Long bookingId) {
        return bookingMapper.toResponse(bookingService.findByIdAndUserId(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponse> getAllByBooker(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestParam(defaultValue = "0", required = false) int from,
                                                @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingMapper.toResponse(bookingService.findAllByBooker(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingResponse> getAllByOwner(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                               @RequestParam(defaultValue = "ALL") BookingState state,
                                               @RequestParam(defaultValue = "0", required = false) int from,
                                               @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingMapper.toResponse(bookingService.findAllByOwner(userId, state, from, size));
    }
}
