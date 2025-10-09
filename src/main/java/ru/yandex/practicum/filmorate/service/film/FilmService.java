package ru.yandex.practicum.filmorate.service.film;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;


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

    public Collection<FilmDto> getFilms() {
        return filmStorage.getFilms().stream().map(FilmMapper::mapToFilmDto).collect(Collectors.toList());
    }

    public void addLike(int id, int userId) {
        Optional<Film> optionalFilm = filmStorage.getFilm(id);
        Optional<User> optionalUser = userStorage.getUser(userId);

        if (optionalFilm.isPresent() && optionalUser.isPresent()) {
            likeStorage.addLike(id, userId);
        }
    }

    public void removeLike(int id, int userId) {
        Optional<Film> optionalFilm = filmStorage.getFilm(id);
        Optional<User> optionalUser = userStorage.getUser(userId);

        if (optionalFilm.isPresent() && optionalUser.isPresent()) {
            likeStorage.removeLike(id, userId);
        }
    }

    public Collection<FilmDto> getMostLikedFilms(int count) {
        Collection<Film> films = filmStorage.getFilms();
        Comparator<Film> comparator = (f1, f2) -> f2.getLikes() - f1.getLikes();

        Collection<Film> f = films.stream().sorted(comparator).limit(count).toList();
        return f.stream().map(FilmMapper::mapToFilmDto).collect(Collectors.toList());
    }
}
