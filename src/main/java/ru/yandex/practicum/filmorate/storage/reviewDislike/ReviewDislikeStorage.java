package ru.yandex.practicum.filmorate.storage.reviewDislike;

public interface ReviewDislikeStorage {
    void addDislike(int reviewId, int userId);

    void removeDislike(int reviewId, int userId);
}
