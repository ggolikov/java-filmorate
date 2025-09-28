package ru.yandex.practicum.filmorate.service.film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilm(int filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
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

    public Collection<Film> getMostLikedFilms(int count) {
        Collection<Film> films = filmStorage.getFilms();
        Comparator<Film> comparator = (f1, f2) -> f2.getLikes().size() - f1.getLikes().size();

        Collection<Film> f = films.stream().sorted(comparator).limit(count).collect(Collectors.toList());
        System.out.println(f);
        return f;
    }
}
