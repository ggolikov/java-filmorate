package ru.yandex.practicum.filmorate.storage.filmGenre;

import java.util.List;

public interface FilmGenreStorage {
    void addFilmGenres(int filmId, List<Integer> genreIds);

    void deleteGenresByFilmId(int filmId);
}
