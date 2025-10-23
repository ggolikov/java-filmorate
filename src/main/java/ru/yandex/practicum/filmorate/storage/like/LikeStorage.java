package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface LikeStorage {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Optional<Integer> findMostSimilarUser(int userId);

    Collection<Film> getRecommendedFilms(int userId, int similarUserId);
}
