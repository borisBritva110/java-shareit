package ru.practicum.shareit.item.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ValidationService validationService;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        userService.getUser(ownerId);

        validationService.validateNotBlank(itemDto.getName(), "name", "item");
        validationService.validateNotBlank(itemDto.getDescription(), "description", "item");
        validationService.validateNotNull(itemDto.getAvailable(), "available", "item");

        if (itemDto.getRequestId() != null) {
            itemRequestRepository.findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Item request with id " + itemDto.getRequestId() + " not found"));
        }

        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        validationService.validateOwner(existingItem.getOwnerId(), ownerId, "update item");

        if (itemDto.getRequestId() != null) {
            itemRequestRepository.findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Item request with id " + itemDto.getRequestId() + " not found"));
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            existingItem.setRequestId(itemDto.getRequestId());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public List<ItemWithBookingsDto> getUserItems(Long ownerId) {
        userService.getUser(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        Map<Long, List<CommentDto>> commentsByItemId = getCommentsByItemIds(itemIds);

        return items.stream()
            .map(item -> {
                ItemWithBookingsDto itemWithBookings = new ItemWithBookingsDto();
                itemWithBookings.setId(item.getId());
                itemWithBookings.setName(item.getName());
                itemWithBookings.setDescription(item.getDescription());
                itemWithBookings.setAvailable(item.getAvailable());
                itemWithBookings.setComments(commentsByItemId.getOrDefault(item.getId(), new ArrayList<>()));

                List<Booking> lastBookings = bookingRepository.findLastBooking(item.getId(), now);
                if (!lastBookings.isEmpty()) {
                    BookingDto lastBookingDto = BookingMapper.toBookingDto(lastBookings.get(0));
                    itemWithBookings.setLastBooking(lastBookingDto);
                }

                List<Booking> nextBookings = bookingRepository.findNextBooking(item.getId(), now);
                if (!nextBookings.isEmpty()) {
                    BookingDto nextBookingDto = BookingMapper.toBookingDto(nextBookings.get(0));
                    itemWithBookings.setNextBooking(nextBookingDto);
                }

                return itemWithBookings;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String searchText = text.toLowerCase();
        return itemRepository
            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                searchText, searchText)
            .stream()
            .filter(Item::getAvailable)
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentCreateDto commentCreateDto, Long userId) {
        userService.getUser(userId);
        itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        List<Booking> userBookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());

        boolean hasBooked = userBookings.stream()
            .anyMatch(booking -> booking.getStatus() == BookingStatus.APPROVED &&
                booking.getEnd().isBefore(LocalDateTime.now()));

        if (!hasBooked) {
            throw new IllegalArgumentException("User can only comment on items they have booked in the past");
        }

        validationService.validateNotBlank(commentCreateDto.getText(), "text", "comment");

        Comment comment = CommentMapper.toComment(commentCreateDto, itemId, userId);
        Comment savedComment = commentRepository.save(comment);

        UserDto author = userService.getUser(userId);
        return CommentMapper.toCommentDto(savedComment, author);
    }

    @Override
    @Transactional
    public ItemWithBookingsDto getItemWithBookingsAndComments(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        ItemWithBookingsDto itemWithBookings = ItemMapper.toItemWithBookingsDto(item);

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
            .map(comment -> {
                UserDto author = userService.getUser(comment.getAuthorId());
                return CommentMapper.toCommentDto(comment, author);
            })
            .collect(Collectors.toList());

        itemWithBookings.setComments(commentDtos);

        if (item.getOwnerId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> lastBookings = bookingRepository.findLastBooking(itemId, now);
            if (!lastBookings.isEmpty()) {
                BookingDto lastBookingDto = BookingMapper.toBookingDto(lastBookings.get(0));
                itemWithBookings.setLastBooking(lastBookingDto);
            }

            List<Booking> nextBookings = bookingRepository.findNextBooking(itemId, now);
            if (!nextBookings.isEmpty()) {
                BookingDto nextBookingDto = BookingMapper.toBookingDto(nextBookings.get(0));
                itemWithBookings.setNextBooking(nextBookingDto);
            }
        }

        return itemWithBookings;
    }

    @Override
    public List<ItemDto> getItemsByRequest(Long requestId) {
        List<Item> items = itemRepository.findByRequestId(requestId);
        return items.stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
    }

    private Map<Long, List<CommentDto>> getCommentsByItemIds(List<Long> itemIds) {
        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);

        return comments.stream()
            .collect(Collectors.groupingBy(
                Comment::getItemId,
                Collectors.mapping(comment -> {
                    UserDto author = userService.getUser(comment.getAuthorId());
                    return CommentMapper.toCommentDto(comment, author);
                }, Collectors.toList())
            ));
    }
}