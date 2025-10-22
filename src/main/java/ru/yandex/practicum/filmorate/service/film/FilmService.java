package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmDirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
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
    private final FilmDirectorStorage filmDirectorStorage;
    private final DirectorStorage directorStorage;
    private final FeedStorage feedStorage;


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

            List<Genre> distinctGenres = filmGenres.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(Genre::getId, genre -> genre, (existing, replacement) -> existing),
                            map -> new ArrayList<>(map.values())
                    ));

            List<Integer> filmGenreIds = distinctGenres.stream().map(Genre::getId).toList();

            for (Integer genreId : filmGenreIds) {
                if (!genreIds.contains(genreId)) {
                    throw new NotFoundException("Жанр с ID " + genreId + " не найден");
                }
            }

            film.setGenres(distinctGenres);
        }

        Film result = filmStorage.addFilm(film);

        if (filmGenres != null && !filmGenres.isEmpty()) {
            List<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId).toList();

            for (Integer genreId : filmGenreIds) {
                filmGenreStorage.addFilmGenre(film.getId(), genreId);
            }
        }

        List<Director> filmDirectors = film.getDirectors();

        if (filmDirectors != null && !filmDirectors.isEmpty()) {
            List<Integer> directorIds = directorStorage.getDirectors().stream().map(Director::getId).toList();
            List<Integer> filmDirectorIds = filmDirectors.stream().map(Director::getId).toList();

            for (Integer directorId : filmDirectorIds) {
                if (!directorIds.contains(directorId)) {
                    throw new NotFoundException("Режиссёр с ID " + directorId + " не найден");
                }
            }

            for (Integer directorId : filmDirectorIds) {
                filmDirectorStorage.addFilmDirector(film.getId(), directorId);
            }
        }

        return result;
    }

    public Film updateFilm(Film film) {
        List<Integer> mpas = mpaStorage.getRatings().stream().map(Mpa::getId).toList();

        if (!mpas.contains(film.getMpa().getId())) {
            throw new NotFoundException("Рейтинг с ID " + film.getMpa().getId() + " не найден");
        }

        List<Genre> filmGenres = film.getGenres();

        if (filmGenres != null && !filmGenres.isEmpty()) {
            List<Integer> genreIds = genreStorage.getGenres().stream().map(Genre::getId).toList();


            List<Genre> distinctGenres = filmGenres.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(Genre::getId, genre -> genre, (existing, replacement) -> existing),
                            map -> new ArrayList<>(map.values())
                    ));

            List<Integer> filmGenreIds = distinctGenres.stream().map(Genre::getId).toList();

            for (Integer genreId : filmGenreIds) {
                if (!genreIds.contains(genreId)) {
                    throw new NotFoundException("Жанр с ID " + genreId + " не найден");
                }
            }
            
            film.setGenres(distinctGenres);
        }

        Film result = filmStorage.updateFilm(film);


        if (filmGenres != null) {

            filmGenreStorage.deleteGenresByFilmId(film.getId());
            if (!filmGenres.isEmpty()) {
                List<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId).toList();
                for (Integer genreId : filmGenreIds) {
                    filmGenreStorage.addFilmGenre(film.getId(), genreId);
                }
            }
        }

        if (film.getDirectors() != null) {
            filmDirectorStorage.deleteDirectorsByFilmId(film.getId());
            if (!film.getDirectors().isEmpty()) {
                List<Integer> filmDirectorIds = film.getDirectors().stream().map(Director::getId).toList();
                for (Integer directorId : filmDirectorIds) {
                    filmDirectorStorage.addFilmDirector(film.getId(), directorId);
                }
            }
        }

        return result;
    }

    public void removeFilm(int id) {
        filmStorage.getFilm(id).orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
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

            Film film = optionalFilm.get();
            Event event = new Event();
            event.setUserId(userId);
            event.setEntityId(film.getId());
            event.setType(EventType.LIKE);
            event.setOperation(Operation.ADD);
            event.setTimestamp(Instant.now().toEpochMilli());

            feedStorage.addEvent(event);
        }
    }

    public void removeLike(int id, int userId) {
        Optional<Film> optionalFilm = filmStorage.getFilm(id);
        Optional<User> optionalUser = userStorage.getUser(userId);

        if (optionalFilm.isPresent() && optionalUser.isPresent()) {
            likeStorage.removeLike(id, userId);

            Film film = optionalFilm.get();
            Event event = new Event();
            event.setUserId(userId);
            event.setEntityId(film.getId());
            event.setType(EventType.LIKE);
            event.setOperation(Operation.REMOVE);
            event.setTimestamp(Instant.now().toEpochMilli());

            feedStorage.addEvent(event);
        }
    }

    public Collection<FilmDto> getMostLikedFilms(int count, Integer genreId, Integer year) {
        if (genreId != null) {
            genreStorage.getGenre(genreId).orElseThrow(() -> new NotFoundException("Жанр с id " + genreId + " не найден"));
        }
        Collection<Film> films = filmStorage.getMostLikedFilms(count, genreId, year);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return films.stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public Collection<FilmDto> getCommonFilms(int userId, int friendId) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        userStorage.getUser(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        return filmStorage.getCommonFilms(userId, friendId)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public Collection<FilmDto> searchFilms(String query, String by) {
        if (!Set.of("title", "director", "title,director","director,title").contains(by)) {
            throw new IllegalArgumentException("Параметр поиска " + by + " не найден");
        }
        return filmStorage.searchFilms(query, by)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
