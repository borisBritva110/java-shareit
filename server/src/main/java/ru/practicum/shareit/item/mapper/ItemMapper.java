package ru.practicum.shareit.item.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;


@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getRequestId()
        );
    }

    public static Item toItem(ItemDto itemDto, Long ownerId) {
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Field 'available' is required and cannot be null");
        }

        return new Item(
            itemDto.getId(),
            itemDto.getName(),
            itemDto.getDescription(),
            itemDto.getAvailable(),
            ownerId,
            itemDto.getRequestId()
        );
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(Item item) {
        return new ItemWithBookingsDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            null,
            null,
            new ArrayList<>()
        );
    }
}