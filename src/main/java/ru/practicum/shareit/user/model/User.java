package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class User {

    private long id;
    private String name;
    private String email;
}