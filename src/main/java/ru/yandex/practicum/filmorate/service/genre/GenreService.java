package ru.yandex.practicum.filmorate.service.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreStorage genreStorage;
    @Autowired
    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre addGenre(Genre genre) {
        return genreStorage.addGenre(genre);
    }

    public Genre getGenre(int genreId) {
        return genreStorage.getGenre(genreId);
    }

    public Collection<Genre> getGenres() {
        return genreStorage.getAllGenres();
    }
}
