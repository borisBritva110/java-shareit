package ru.practicum.shareit.item.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.updateItem(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItem(@PathVariable Long itemId,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getItemWithBookingsAndComments(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getUserItems(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.getUserItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestBody CommentCreateDto commentCreateDto,
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.addComment(itemId, commentCreateDto, userId);
    }
}