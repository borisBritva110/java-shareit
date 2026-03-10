package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
        String name, String description);

    List<Item> findByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);
}