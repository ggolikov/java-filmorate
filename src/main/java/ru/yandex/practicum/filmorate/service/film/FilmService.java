package ru.yandex.practicum.filmorate.service.film;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage , UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilm(int filmId) {
        return filmStorage.getFilm(filmId);
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

    public void addLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);

        if (film != null && user != null) {
            Set<Integer> likes = film.getLikes();
            likes.add(userId);
        }
    }

    public void removeLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);

        if (film != null && user != null) {
            Set<Integer> likes = film.getLikes();
            likes.remove(userId);
        }
    }

    public Collection<Film> getMostLikedFilms(int likesCount) {
        Collection<Film> films = filmStorage.getFilms();

        return films.stream().filter(f -> f.getLikes().size() >= likesCount).collect(Collectors.toList());
    }
}
