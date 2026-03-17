package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                            @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return bookingClient.getOwnerBookings(ownerId, state, from, size);
    }
}
