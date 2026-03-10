package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ValidationService validationService;

    @Override
    @Transactional
    public ItemRequestWithItemsDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("User with id " + requestorId + " not found"));
        
        validationService.validateNotBlank(itemRequestCreateDto.getDescription(), "description", "item request");

        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestWithItemsDto(savedRequest, List.of());
    }

    @Override
    public List<ItemRequestWithItemsDto> getOwnItemRequests(Long requestorId) {
        userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("User with id " + requestorId + " not found"));
        
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);
        return getItemRequestsWithItems(requests);
    }

    @Override
    public List<ItemRequestWithItemsDto> getOtherUsersItemRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        
        validationService.validateNotNull(from, "from", "pagination");
        validationService.validateNotNull(size, "size", "pagination");

        if (from < 0) {
            throw new IllegalArgumentException("From parameter cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size parameter must be positive");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable);
        return getItemRequestsWithItems(requests);
    }

    @Override
    public ItemRequestWithItemsDto getItemRequestById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request with id " + requestId + " not found"));
        
        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        
        return itemRequestMapper.toItemRequestWithItemsDto(itemRequest, itemDtos);
    }

    private List<ItemRequestWithItemsDto> getItemRequestsWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        
        Map<Long, List<Item>> itemsByRequestId = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        
        return requests.stream()
                .map(request -> {
                    List<ItemDto> itemDtos = itemsByRequestId.getOrDefault(request.getId(), List.of()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return itemRequestMapper.toItemRequestWithItemsDto(request, itemDtos);
                })
                .collect(Collectors.toList());
    }
}