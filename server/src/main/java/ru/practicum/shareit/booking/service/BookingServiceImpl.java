package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        userService.getUser(userId);

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
            .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available for booking");
        }

        if (item.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Owner cannot book their own item");
        }

        if (bookingRequestDto.getStart() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        if (bookingRequestDto.getEnd() == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) ||
            bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd())) {
            throw new IllegalArgumentException("Invalid booking dates");
        }

        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        if (bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto, userId);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        ItemDto itemDto = itemService.getItem(item.getId());
        UserDto bookerDto = userService.getUser(userId);

        return BookingMapper.toBookingResponseDto(savedBooking, itemDto, bookerDto);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
            .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwnerId().equals(ownerId)) {
            throw new BadRequestException("Only item owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking is not waiting for approval");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        return getBookingResponse(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
            .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!booking.getBookerId().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        return getBookingResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getUserBookings(Long userId, BookingState state, Integer from, Integer size) {
        userService.getUser(userId);
        if (from < 0) {
            throw new IllegalArgumentException("From parameter cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size parameter must be positive");
        }

        List<Booking> bookings = getBookingsByState(userId, state, false);
        return getBookingsResponse(bookings).stream()
            .skip(from)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, BookingState state, Integer from, Integer size) {
        userService.getUser(ownerId);
        if (from < 0) {
            throw new IllegalArgumentException("From parameter cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size parameter must be positive");
        }

        List<Booking> bookings = getBookingsByState(ownerId, state, true);
        return getBookingsResponse(bookings).stream()
            .skip(from)
            .limit(size)
            .collect(Collectors.toList());
    }

    private BookingResponseDto getBookingResponse(Booking booking) {
        ItemDto itemDto = itemService.getItem(booking.getItemId());
        UserDto bookerDto = userService.getUser(booking.getBookerId());
        return BookingMapper.toBookingResponseDto(booking, itemDto, bookerDto);
    }

    private List<BookingResponseDto> getBookingsResponse(List<Booking> bookings) {
        return bookings.stream()
            .map(this::getBookingResponse)
            .collect(Collectors.toList());
    }

    private List<Booking> getBookingsByState(Long userId, BookingState state, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = null;

        if (isOwner) {
            List<Item> ownerItems = itemRepository.findByOwnerId(userId);
            if (ownerItems.isEmpty()) {
                throw new NotFoundException("User has no items");
            }
            itemIds = ownerItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        }

        switch (state) {
            case CURRENT:
                return isOwner ? bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                    itemIds, now, now) : bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, now, now);
            case PAST:
                return isOwner ? bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemIds, now) :
                    bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE:
                return isOwner ? bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemIds, now) :
                    bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING:
                return isOwner ? bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.WAITING) :
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED:
                return isOwner ? bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.REJECTED) :
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default:
                return isOwner ? bookingRepository.findByItemIdInOrderByStartDesc(itemIds) :
                    bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }
    }
}