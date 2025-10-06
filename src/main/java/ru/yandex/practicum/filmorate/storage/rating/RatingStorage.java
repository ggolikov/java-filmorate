package ru.yandex.practicum.filmorate.storage.rating;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

public interface RatingStorage {
    Rating getRating(int id);

    Collection<Rating> getAllRatings();
}
