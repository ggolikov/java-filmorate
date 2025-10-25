package ru.yandex.practicum.filmorate.storage.filmDirector;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface FilmDirectorStorage {
    void addFilmDirectors(int filmId, List<Integer> directorIds);

    void deleteDirectorsByFilmId(int filmId);

    List<Director> getDirectorsByFilmId(int filmId);
}
