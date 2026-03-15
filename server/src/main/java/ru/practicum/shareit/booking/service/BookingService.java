package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto approveBooking(Long bookingId, boolean approved, Long ownerId);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getUserBookings(Long userId, BookingState state, Integer from, Integer size);

    List<BookingResponseDto> getOwnerBookings(Long ownerId, BookingState state, Integer from, Integer size);
}