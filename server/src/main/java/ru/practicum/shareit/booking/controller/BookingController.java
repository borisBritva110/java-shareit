package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto createBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                            @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return bookingService.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable Long bookingId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state);
        return bookingService.getUserBookings(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state);
        return bookingService.getOwnerBookings(ownerId, bookingState, from, size);
    }
}