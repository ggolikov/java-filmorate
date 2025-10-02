package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film getFilm(int id);

    Film addFilm(@Valid Film film);

    Film updateFilm(@Valid Film film);

    void removeFilm(int id);

    Collection<Film> getFilms();

    void validate(Film film) throws ValidationException;
}
