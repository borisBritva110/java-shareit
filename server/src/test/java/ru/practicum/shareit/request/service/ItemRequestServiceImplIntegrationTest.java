package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequest_shouldCreateAndReturnRequest() {
        Long userId = 1L;
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a new laptop for work");
        ItemRequestWithItemsDto createdRequest = itemRequestService.createItemRequest(createDto, userId);
        assertNotNull(createdRequest);
        assertNotNull(createdRequest.getId());
        assertEquals("Need a new laptop for work", createdRequest.getDescription());
        assertNotNull(createdRequest.getCreated());
    }

    @Test
    void getItemRequestById_shouldReturnRequestWithItems() {
        Long requestId = 1L;
        Long userId = 2L;
        ItemRequestWithItemsDto request = itemRequestService.getItemRequestById(requestId, userId);
        assertNotNull(request);
        assertEquals(requestId, request.getId());
        assertEquals("Need a drill", request.getDescription());
        assertNotNull(request.getCreated());
        assertNotNull(request.getItems());
    }

    @Test
    void getOwnItemRequests_shouldReturnUserRequests() {
        Long userId = 2L;
        List<ItemRequestWithItemsDto> requests = itemRequestService.getOwnItemRequests(userId);
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
    }

    @Test
    void getOtherUsersItemRequests_shouldReturnAllRequestsExceptUser() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequestWithItemsDto> requests = itemRequestService.getOtherUsersItemRequests(userId, from, size);
        assertNotNull(requests);
    }
}