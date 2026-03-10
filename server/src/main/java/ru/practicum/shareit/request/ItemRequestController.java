package ru.practicum.shareit.request;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestWithItemsDto createItemRequest(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                     @RequestHeader(USER_ID_HEADER) Long requestorId) {
        return itemRequestService.createItemRequest(itemRequestCreateDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getOwnItemRequests(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        return itemRequestService.getOwnItemRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getOtherUsersItemRequests(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getOtherUsersItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getItemRequestById(@PathVariable Long requestId,
                                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}