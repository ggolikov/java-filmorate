package ru.yandex.practicum.filmorate.storage.review;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> getReview(int id);

    Review addReview(@Valid Review review);

    Review updateReview(@Valid Review review);

    void removeReview(int id);

    Collection<Review> getReviews();

    Collection<Review> getReviews(int filmId, int count);
}
