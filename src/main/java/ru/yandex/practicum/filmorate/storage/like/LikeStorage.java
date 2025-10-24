package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.Optional;

public interface LikeStorage {
    public Optional<Like> getLike(int filmId, int usrId);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Optional<Integer> findMostSimilarUser(int userId);

    Collection<Film> getRecommendedFilms(int userId, int similarUserId);
}
