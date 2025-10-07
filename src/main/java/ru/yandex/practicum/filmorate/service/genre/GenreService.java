package ru.yandex.practicum.filmorate.service.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
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

    public GenreDto getGenre(int genreId) {
        return genreStorage.getGenre(genreId).map(GenreMapper::mapToGenreDto).orElseThrow(() -> new NotFoundException("Жанр не найден с ID: " + genreId));

    }

    public Collection<Genre> getGenres() {
        return genreStorage.getGenres();
    }
}
