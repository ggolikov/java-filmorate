package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> getFilm(int id);

    Film addFilm(@Valid Film film);

    Film updateFilm(@Valid Film film);

    void removeFilm(int id);

    Collection<Film> getFilms();

    void validate(Film film) throws ValidationException;

    List<Film> getCommonFilms(int userId, int friendId);

    Collection<Film> getMostLikedFilms(int count, Integer genreId, Integer year);
}
