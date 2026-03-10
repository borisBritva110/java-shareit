package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestWithItemsDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Long requestorId);
    
    List<ItemRequestWithItemsDto> getOwnItemRequests(Long requestorId);
    
    List<ItemRequestWithItemsDto> getOtherUsersItemRequests(Long userId, Integer from, Integer size);
    
    ItemRequestWithItemsDto getItemRequestById(Long requestId, Long userId);
}