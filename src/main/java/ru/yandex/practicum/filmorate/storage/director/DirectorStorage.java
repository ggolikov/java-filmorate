package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Optional<Director> getDirector(int id);

    Collection<Director> getDirectors();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void removeDirector(int id);
}
