package ru.practicum.shareit.item.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemDto createItem(long userId, ItemDto itemCreateDto) {
        User user = getUserOrElseThrow(userId);

        Item item = ItemMapper.toItem(itemCreateDto);
        item.setOwner(user);
        Item createdItem = itemRepository.createItem(item);

        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto getItem(long id) {
        Item item = getItemOrElseThrow(id);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> searchItems(String search) {
        if (search == null || search.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchItems(search.toLowerCase())
            .stream()
            .map(ItemMapper::toItemDto)
            .toList();
    }

    public List<ItemDto> getUserItems(long userId) {
        getUserOrElseThrow(userId);
        return itemRepository.getUserItems(userId)
            .stream()
            .map(ItemMapper::toItemDto)
            .toList();
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemUpdateDto) {
        getUserOrElseThrow(userId);
        Item item = getItemOrElseThrow(itemId);
        throwIfUserNotItemOwner(userId, item);

        updateItemFields(item, itemUpdateDto);
        itemRepository.updateItem(item);

        return ItemMapper.toItemDto(item);
    }

    private void updateItemFields(Item item, ItemDto itemUpdateDto) {
        String name = itemUpdateDto.getName();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }

        String description = itemUpdateDto.getDescription();
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }

        Boolean available = itemUpdateDto.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }
    }

    private User getUserOrElseThrow(long id) {
        return userRepository.getUser(id)
            .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private Item getItemOrElseThrow(long id) {
        return itemRepository.getItem(id)
            .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));
    }

    private void throwIfUserNotItemOwner(long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new SecurityException("User " + userId + " is not the owner of item " + item.getId());
        }
    }
}