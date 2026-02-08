package ru.practicum.shareit.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.item.model.Item;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long id;

    @Override
    public Item createItem(Item item) {
        item.setId(++id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> getItem(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> searchItems(String search) {
        return items.values().stream()
            .filter(Item::getAvailable)
            .filter(item -> item.getName().toLowerCase().contains(search)
                || item.getDescription().toLowerCase().contains(search))
            .toList();
    }

    @Override
    public List<Item> getUserItems(long userId) {
        return items.values().stream()
            .filter(item -> item.getOwner().getId() == userId)
            .toList();
    }

    @Override
    public void updateItem(Item item) {
        items.put(item.getId(), item);
    }
}
