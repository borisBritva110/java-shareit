package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Test
    void createItem_shouldCreateAndReturnItem() {
        Long ownerId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Integration Test Item");
        itemDto.setDescription("Integration test description");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.createItem(itemDto, ownerId);
        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals("Integration Test Item", createdItem.getName());
        assertEquals("Integration test description", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
    }

    @Test
    void updateItem_shouldUpdateExistingItem() {
        Long itemId = 1L;
        Long ownerId = 1L;
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Item Name");
        updateDto.setDescription("Updated description");
        updateDto.setAvailable(false);
        ItemDto updatedItem = itemService.updateItem(itemId, updateDto, ownerId);
        assertNotNull(updatedItem);
        assertEquals(itemId, updatedItem.getId());
        assertEquals("Updated Item Name", updatedItem.getName());
        assertEquals("Updated description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void getItemById_shouldReturnItem() {
        Long itemId = 1L;
        ItemDto item = itemService.getItem(itemId);
        assertNotNull(item);
        assertEquals(itemId, item.getId());
        assertEquals("Test Item 1", item.getName());
        assertEquals("Description 1", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void searchItems_shouldReturnAvailableItems() {
        String searchText = "Test";
        List<ItemDto> items = itemService.searchItems(searchText);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        items.forEach(item -> {
            assertTrue(item.getAvailable());
            assertTrue(item.getName().toLowerCase().contains("test") || 
                      item.getDescription().toLowerCase().contains("test"));
        });
    }

    @Test
    void addComment_shouldAddCommentToItem() {
        Long itemId = 1L;
        Long authorId = 2L;
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Great item, thanks!")
                .build();
        CommentDto comment = itemService.addComment(itemId, commentCreateDto, authorId);
        assertNotNull(comment);
        assertNotNull(comment.getId());
        assertEquals("Great item, thanks!", comment.getText());
        assertEquals("Test User 2", comment.getAuthorName());
    }
}