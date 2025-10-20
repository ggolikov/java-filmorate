package ru.yandex.practicum.filmorate.storage.reviewDislike;

import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Optional;

public interface ReviewDislikeStorage {
    Optional<ReviewLike> getDislike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void removeDislike(int reviewId, int userId);
}
