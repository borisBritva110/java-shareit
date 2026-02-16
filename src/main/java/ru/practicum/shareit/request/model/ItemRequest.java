package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class ItemRequest {

    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}