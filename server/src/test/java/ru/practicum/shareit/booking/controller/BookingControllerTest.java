package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        long userId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setAvailable(true);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("Test User");

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
            .id(1L)
            .start(start)
            .end(end)
            .status(String.valueOf(BookingStatus.WAITING))
            .booker(userDto)
            .item(itemDto)
            .build();

        when(bookingService.createBooking(any(BookingRequestDto.class), eq(userId))).thenReturn(bookingResponseDto);
        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.status").value("WAITING"))
            .andExpect(jsonPath("$.booker.id").value(userId))
            .andExpect(jsonPath("$.item.id").value(1L));

        verify(bookingService, times(1)).createBooking(any(BookingRequestDto.class), eq(userId));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        boolean approved = true;

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setAvailable(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("Booker");

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
            .id(bookingId)
            .status(String.valueOf(BookingStatus.APPROVED))
            .booker(userDto)
            .item(itemDto)
            .build();

        when(bookingService.approveBooking(bookingId, approved, userId)).thenReturn(bookingResponseDto);
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", userId)
                .param("approved", String.valueOf(approved)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(bookingId))
            .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).approveBooking(bookingId, approved, userId);
    }

    @Test
    void getUserBookings_shouldReturnUserBookings() throws Exception {
        Long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;

        List<BookingResponseDto> bookings = List.of(
            BookingResponseDto.builder().id(1L).status(String.valueOf(BookingStatus.APPROVED)).build(),
            BookingResponseDto.builder().id(2L).status(String.valueOf(BookingStatus.WAITING)).build()
        );

        when(bookingService.getUserBookings(userId, BookingState.valueOf(state), from, size)).thenReturn(bookings);
        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", userId)
                .param("state", state)
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));

        verify(bookingService, times(1)).getUserBookings(userId, BookingState.valueOf(state), from, size);
    }
}