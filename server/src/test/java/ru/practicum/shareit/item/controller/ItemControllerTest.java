package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto createdItem = new ItemDto();
        createdItem.setId(1L);
        createdItem.setName("Test Item");
        createdItem.setDescription("Test Description");
        createdItem.setAvailable(true);

        when(itemService.createItem(any(ItemDto.class), eq(userId))).thenReturn(createdItem);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Test Item"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).createItem(any(ItemDto.class), eq(userId));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Item");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(itemId);
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Updated Description");
        updatedItem.setAvailable(false);

        when(itemService.updateItem(eq(itemId), any(ItemDto.class), eq(userId))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(itemId))
            .andExpect(jsonPath("$.name").value("Updated Item"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.available").value(false));

        verify(itemService, times(1)).updateItem(eq(itemId), any(ItemDto.class), eq(userId));
    }

    @Test
    void searchItems_shouldReturnSearchResults() throws Exception {
        String searchText = "test";
        Long userId = 1L;

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setAvailable(true);

        List<ItemDto> items = List.of(item1, item2);

        when(itemService.searchItems(searchText)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                .param("text", searchText)
                .header("X-Sharer-User-Id", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("Test Item 1"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].name").value("Test Item 2"));

        verify(itemService, times(1)).searchItems(searchText);
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        Long itemId = 1L;
        Long userId = 2L;
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
            .text("Great item!")
            .build();

        CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("Great item!")
            .authorName("Test User")
            .build();

        when(itemService.addComment(eq(itemId), any(CommentCreateDto.class), eq(userId))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.text").value("Great item!"))
            .andExpect(jsonPath("$.authorName").value("Test User"));

        verify(itemService, times(1)).addComment(eq(itemId), any(CommentCreateDto.class), eq(userId));
    }
}