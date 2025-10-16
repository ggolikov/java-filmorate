package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    public static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 12, 28);

    private static final String GET_FILM_BASIC_QUERY = """
            SELECT f.*, m.name as mpa_name
            FROM films AS f
            LEFT JOIN mpas as m ON f.MPA_ID = m.ID
            WHERE f.id = ?
            """;

    private static final String GET_ALL_FILMS_BASIC_QUERY = """
            SELECT f.*, m.name as mpa_name
            FROM films AS f
            LEFT JOIN mpas as m ON f.MPA_ID = m.ID
            """;

    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films(name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;

    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String GET_FILM_LIKES_COUNT = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
    private static final String GET_FILM_GENRES = """
            SELECT g.id, g.name
            FROM genres g
            JOIN films_genres fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            """;
    private static final String GET_FILM_DIRECTORS = """
            SELECT d.id, d.name
            FROM directors d
            JOIN films_directors fd ON d.id = fd.director_id
            WHERE fd.film_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new FilmRowMapper());
        this.jdbcTemplate = jdbc;
    }

    @Override
    public Optional<Film> getFilm(int id) {
        Optional<Film> filmOpt = findOne(GET_FILM_BASIC_QUERY, id);

        if (filmOpt.isPresent()) {
            Film film = filmOpt.get();
            loadFilmAdditionalData(film);
            return Optional.of(film);
        }

        return Optional.empty();
    }

    @Override
    public Film addFilm(Film film) {
        validate(film);
        int id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        List<Integer> ids = getFilms().stream().mapToInt(Film::getId).boxed().toList();

        if (!ids.contains(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        validate(film);

        update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        return film;
    }

    @Override
    public void removeFilm(int id) {
        update(DELETE_FILM_QUERY, id);
    }

    @Override
    public Collection<Film> getFilms() {
        Collection<Film> films = findMany(GET_ALL_FILMS_BASIC_QUERY);

        for (Film film : films) {
            loadFilmAdditionalData(film);
        }

        return films;
    }

    @Override
    public void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Поле name не может быть пустым или null");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Длина поля description не должна превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(MIN_FILM_DATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = """
                SELECT DISTINCT f.*, m.name as mpa_name
                FROM films f
                JOIN likes l1 ON f.id = l1.film_id AND l1.user_id = ?
                JOIN likes l2 ON f.id = l2.film_id AND l2.user_id = ?
                LEFT JOIN mpas m ON f.mpa_id = m.id
                """;

        List<Film> films = findMany(sql, userId, friendId);
        for (Film film : films) {
            loadFilmAdditionalData(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        String baseQuery = """
                SELECT f.*, m.name as mpa_name
                FROM films f
                JOIN films_directors fd ON f.id = fd.film_id
                LEFT JOIN mpas m ON f.mpa_id = m.id
                WHERE fd.director_id = ?
                """;

        String orderBy;
        if ("year".equalsIgnoreCase(sortBy)) {
            orderBy = " ORDER BY f.release_date";
        } else if ("likes".equalsIgnoreCase(sortBy)) {
            orderBy = " ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC";
        } else {
            throw new IllegalArgumentException("Неверный параметр sortBy: " + sortBy);
        }

        String sql = baseQuery + orderBy;
        List<Film> films = findMany(sql, directorId);

        for (Film film : films) {
            loadFilmAdditionalData(film);
        }

        return films;
    }


    private void loadFilmAdditionalData(Film film) {
        int filmId = film.getId();

        Integer likesCount = jdbcTemplate.queryForObject(GET_FILM_LIKES_COUNT, Integer.class, filmId);
        film.setLikes(likesCount);


        List<ru.yandex.practicum.filmorate.model.Genre> genres = jdbcTemplate.query(
                GET_FILM_GENRES,
                (rs, rowNum) -> {
                    ru.yandex.practicum.filmorate.model.Genre genre = new ru.yandex.practicum.filmorate.model.Genre();
                    genre.setId(rs.getInt("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                },
                filmId
        );
        film.setGenres(genres);

        List<ru.yandex.practicum.filmorate.model.Director> directors = jdbcTemplate.query(
                GET_FILM_DIRECTORS,
                (rs, rowNum) -> {
                    ru.yandex.practicum.filmorate.model.Director director = new ru.yandex.practicum.filmorate.model.Director();
                    director.setId(rs.getInt("id"));
                    director.setName(rs.getString("name"));
                    return director;
                },
                filmId
        );
        film.setDirectors(directors);
    }
}