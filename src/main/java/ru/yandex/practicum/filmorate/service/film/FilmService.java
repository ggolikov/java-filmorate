package ru.yandex.practicum.filmorate.service.film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage")
            FilmStorage filmStorage,
            @Qualifier("userDbStorage")
            UserStorage userStorage,
            @Qualifier("genreDbStorage")
            GenreStorage genreStorage,
            @Qualifier("mpaDbStorage")
            MpaStorage mpaStorage,
            @Qualifier("filmGenreDbStorage")
            FilmGenreStorage filmGenreStorage
            ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
    }

    public FilmDto getFilm(int filmId) {
        return filmStorage.getFilm(filmId).map(FilmMapper::mapToFilmDto).orElseThrow(() -> new NotFoundException("Фильм не найден с ID: " + filmId));
    }

    public Film addFilm(Film film) {
        List<Integer> mpas = mpaStorage.getRatings().stream().map(Mpa::getId).toList();

        if (!mpas.contains(film.getMpa().getId())) {
            throw new NotFoundException("Рейтинг с ID " + film.getMpa().getId() + " не найден");
        }

        List<Genre> filmGenres = film.getGenres();

        if (filmGenres != null && !filmGenres.isEmpty()) {
            List<Integer> genreIds = genreStorage.getGenres().stream().map(Genre::getId).toList();
            List<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId).toList();

            for (Integer genreId : filmGenreIds) {
                if (!genreIds.contains(genreId)) {
                    throw new NotFoundException("Жанр с ID " + genreId + " не найден");
                }
            }
        }
        Film result = filmStorage.addFilm(film);

        if (filmGenres != null && !filmGenres.isEmpty()) {
            List<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId).toList();

            for (Integer genreId : filmGenreIds) {
                 filmGenreStorage.addFilmGenre(film.getId(), genreId);
            }
        }

        return result;
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
        Optional<Film> optionalFilm = filmStorage.getFilm(id);
        Optional<User> optionalUser = userStorage.getUser(userId);

        if (optionalFilm.isPresent() && optionalUser.isPresent()) {
            Film film = optionalFilm.get();
            Set<Integer> likes = film.getLikes();
            likes.add(userId);
        }
    }

    public void removeLike(int id, int userId) {
        Optional<Film> optionalFilm = filmStorage.getFilm(id);
        Optional<User> optionalUser = userStorage.getUser(userId);

        if (optionalFilm.isPresent() && optionalUser.isPresent()) {
            Film film = optionalFilm.get();
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
