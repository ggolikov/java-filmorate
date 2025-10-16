package ru.yandex.practicum.filmorate.storage.filmDirector;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface FilmDirectorStorage {
    void addFilmDirector(int filmId, int directorId);

    void deleteDirectorsByFilmId(int filmId);

    List<Director> getDirectorsByFilmId(int filmId);
}
