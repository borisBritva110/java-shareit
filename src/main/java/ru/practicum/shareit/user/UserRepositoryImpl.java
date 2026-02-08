package ru.practicum.shareit.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.user.model.User;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public User createUser(User user) {
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.values().stream()
            .filter(user -> user.getEmail().equals(email))
            .findFirst();
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }
}