package ru.yandex.practicum.filmorate.storage.genre;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> getGenre(int id);

    Collection<Genre> getGenres();
}
