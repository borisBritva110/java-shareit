package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_shouldCreateAndReturnBooking() {
        Long bookerId = 3L;
        Long itemId = 2L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(itemId);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto createdBooking = bookingService.createBooking(bookingRequestDto, bookerId);
        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getId());
        assertEquals(itemId, createdBooking.getItem().getId());
        assertEquals(bookerId, createdBooking.getBooker().getId());
        assertEquals(BookingStatus.WAITING.toString(), createdBooking.getStatus());
    }

    @Test
    void approveBooking_shouldApproveWaitingBooking() {
        Long bookingId = 2L;
        Long ownerId = 1L;
        boolean approved = true;
        BookingResponseDto approvedBooking = bookingService.approveBooking(bookingId, approved, ownerId);
        assertNotNull(approvedBooking);
        assertEquals(bookingId, approvedBooking.getId());
        assertEquals(BookingStatus.APPROVED.toString(), approvedBooking.getStatus());
    }

    @Test
    void getBooking_shouldReturnBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        BookingResponseDto booking = bookingService.getBooking(bookingId, userId);
        assertNotNull(booking);
        assertEquals(bookingId, booking.getId());
        assertEquals(1L, booking.getItem().getId());
        assertEquals(2L, booking.getBooker().getId());
        assertEquals(BookingStatus.APPROVED.toString(), booking.getStatus());
    }

    @Test
    void getUserBookings_shouldReturnUserBookings() {
        Long userId = 2L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;
        List<BookingResponseDto> bookings = bookingService.getUserBookings(userId, state, from, size);
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        bookings.forEach(booking -> assertEquals(userId, booking.getBooker().getId()));
    }

    @Test
    void getOwnerBookings_shouldReturnOwnerBookings() {
        Long ownerId = 1L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;
        List<BookingResponseDto> bookings = bookingService.getOwnerBookings(ownerId, state, from, size);
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertTrue(bookings.size() > 0);
    }
}