package ru.practicum.shareit.validation;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Service
public class ValidationService {
    
    public void validateUserExists(User user, Long userId) {
        if (user == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }
    
    public void validateItemExists(Item item, Long itemId) {
        if (item == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
    }
    
    public void validateItemRequestExists(ItemRequest itemRequest, Long requestId) {
        if (itemRequest == null) {
            throw new NotFoundException("Item request with id " + requestId + " not found");
        }
    }
    
    public void validateOwner(Long ownerId, Long userId, String entityType) {
        if (!ownerId.equals(userId)) {
            throw new NotFoundException("Access denied: only owner can " + entityType);
        }
    }
    
    public void validateNotNull(Object value, String fieldName, String entityType) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null for " + entityType);
        }
    }
    
    public void validateNotBlank(String value, String fieldName, String entityType) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank for " + entityType);
        }
    }
}