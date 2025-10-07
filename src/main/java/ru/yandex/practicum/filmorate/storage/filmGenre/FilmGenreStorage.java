package ru.yandex.practicum.filmorate.storage.filmGenre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

public interface FilmGenreStorage {
    void addFilmGenre(int filmId, int genreId);
}
