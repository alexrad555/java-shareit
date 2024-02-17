package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(X_SHARER_USER_ID) Long userId,
											  @RequestParam(defaultValue = "ALL") BookingState state,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwner(@RequestHeader(X_SHARER_USER_ID) Long userId,
											    @RequestParam(defaultValue = "ALL") BookingState state,
												@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
												@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get booking by owner with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookingsByOwner(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Long userId,
										 @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(@RequestHeader(X_SHARER_USER_ID) Long userId,
								 		 @RequestParam() boolean approved,
								 		 @PathVariable Long bookingId) {
		log.info("Updating userId={}", userId);
		return bookingClient.update(bookingId, userId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}
}
