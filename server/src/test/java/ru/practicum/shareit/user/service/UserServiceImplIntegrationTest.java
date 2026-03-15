package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    void createUser_shouldCreateAndReturnUser() {
        UserDto userDto = UserDto.builder()
                .name("Integration Test User")
                .email("integration@test.com")
                .build();
        UserDto createdUser = userService.createUser(userDto);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("Integration Test User", createdUser.getName());
        assertEquals("integration@test.com", createdUser.getEmail());
    }

    @Test
    void getUser_shouldReturnUser() {
        Long userId = 1L;
        UserDto user = userService.getUser(userId);
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("Test User 1", user.getName());
        assertEquals("testuser1@example.com", user.getEmail());
    }
}