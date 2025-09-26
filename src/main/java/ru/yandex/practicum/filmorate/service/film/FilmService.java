package ru.yandex.practicum.filmorate.service.film;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final int LIKES_COUNT = 10;

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(@Valid Film film) {
        return filmStorage.addFilm(film);
    };

    public Film updateFilm(@Valid Film film) {
        return filmStorage.updateFilm(film);
    }

    public void removeFilm(int id) {
        filmStorage.removeFilm(id);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);

        if (film != null) {
            Set<Integer> likes = film.getLikes();
            likes.add(userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            Set<Integer> likes = film.getLikes();
            likes.remove(userId);
        }
    }

    public Collection<Film> getMostLikedFilms() {
        Collection<Film> films = filmStorage.getFilms();

        return films.stream().filter(f -> f.getLikes().size() > LIKES_COUNT).collect(Collectors.toList());
    }
}
