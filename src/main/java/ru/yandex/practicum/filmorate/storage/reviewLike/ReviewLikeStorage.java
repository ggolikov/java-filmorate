package ru.yandex.practicum.filmorate.storage.reviewLike;

import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Optional;

public interface ReviewLikeStorage {
    Optional<ReviewLike> getLike(int reviewId, int userId);

    void addLike(int reviewId, int userId);

    void removeLike(int reviewId, int userId);
}
