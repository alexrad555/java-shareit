package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.controller.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingMapper bookingMapper;
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse create(@Valid @RequestBody BookingCreateRequest bookingDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingMapper.toResponse(bookingService.create(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam() boolean approved,
                                  @PathVariable Long bookingId) {
        return bookingMapper.toResponse(bookingService.update(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        return bookingMapper.toResponse(bookingService.findByIdAndUserId(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponse> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingMapper.toResponse(bookingService.findAllByBooker(userId, state));
    }

    @GetMapping("/owner")
    public List<BookingResponse> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingMapper.toResponse(bookingService.findAllByOwner(userId, state));
    }
}
