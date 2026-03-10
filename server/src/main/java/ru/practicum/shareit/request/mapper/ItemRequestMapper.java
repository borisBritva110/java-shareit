package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequestWithItemsDto toItemRequestWithItemsDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestWithItemsDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }
}