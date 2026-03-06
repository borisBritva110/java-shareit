package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    List<Comment> findByItemIdIn(List<Long> itemIds);

    boolean existsByItemIdAndAuthorId(Long itemId, Long authorId);
}