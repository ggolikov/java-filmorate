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

    private static final String GET_FILM_QUERY = """
            SELECT
                f.*,
                m.name AS mpa_name,
                array_agg(DISTINCT g.id ORDER BY g.id) AS genre_ids,
                array_agg(DISTINCT g.name ORDER BY g.id) AS genre_names,
                array_agg(DISTINCT d.id ORDER BY d.id) AS director_ids,
                array_agg(DISTINCT d.name ORDER BY d.id) AS director_names,
                COUNT(DISTINCT l.user_id) AS likes_count
            FROM films f
            LEFT JOIN mpas m ON f.mpa_id = m.id
            LEFT JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN films_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE f.id = ?
            GROUP BY f.id, m.name
            """;

    private static final String GET_ALL_FILMS_QUERY = """
            SELECT
                f.*,
                m.name AS mpa_name,
                array_agg(DISTINCT g.id ORDER BY g.id) AS genre_ids,
                array_agg(DISTINCT g.name ORDER BY g.id) AS genre_names,
                array_agg(DISTINCT d.id ORDER BY d.id) AS director_ids,
                array_agg(DISTINCT d.name ORDER BY d.id) AS director_names,
                COUNT(DISTINCT l.user_id) AS likes_count
            FROM films f
            LEFT JOIN mpas m ON f.mpa_id = m.id
            LEFT JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN films_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id, m.name
            """;

    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;

    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";

    public FilmDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new FilmRowMapper());
    }

    @Override
    public Optional<Film> getFilm(int id) {
        return findOne(GET_FILM_QUERY, id);
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
        validate(film);
        List<Integer> ids = getFilms().stream().map(Film::getId).toList();
        if (!ids.contains(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        update(
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public void removeFilm(int id) {
        update(DELETE_FILM_QUERY, id);
    }

    @Override
    public Collection<Film> getFilms() {
        return findMany(GET_ALL_FILMS_QUERY);
    }

    @Override
    public void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Поле name не может быть пустым или null");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Длина поля description не должна превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(MIN_FILM_DATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = """
                SELECT
                    f.*,
                    m.name AS mpa_name,
                    array_agg(DISTINCT g.id ORDER BY g.id) AS genre_ids,
                    array_agg(DISTINCT g.name ORDER BY g.id) AS genre_names,
                    array_agg(DISTINCT d.id ORDER BY d.id) AS director_ids,
                    array_agg(DISTINCT d.name ORDER BY d.id) AS director_names,
                    COUNT(DISTINCT l.user_id) AS likes_count
                FROM films f
                JOIN likes l1 ON f.id = l1.film_id AND l1.user_id = ?
                JOIN likes l2 ON f.id = l2.film_id AND l2.user_id = ?
                LEFT JOIN mpas m ON f.mpa_id = m.id
                LEFT JOIN films_genres fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                LEFT JOIN films_directors fd ON f.id = fd.film_id
                LEFT JOIN directors d ON fd.director_id = d.id
                LEFT JOIN likes l ON f.id = l.film_id
                GROUP BY f.id, m.name
                ORDER BY likes_count DESC
                """;
        return findMany(sql, userId, friendId);
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        String orderBy = switch (sortBy.toLowerCase()) {
            case "year" -> "ORDER BY f.release_date";
            case "likes" -> "ORDER BY COUNT(DISTINCT l.user_id) DESC";
            default -> throw new IllegalArgumentException("Неверный параметр sortBy: " + sortBy);
        };

        String sql = """
                SELECT
                    f.*,
                    m.name AS mpa_name,
                    array_agg(DISTINCT g.id ORDER BY g.id) AS genre_ids,
                    array_agg(DISTINCT g.name ORDER BY g.id) AS genre_names,
                    array_agg(DISTINCT d.id ORDER BY d.id) AS director_ids,
                    array_agg(DISTINCT d.name ORDER BY d.id) AS director_names,
                    COUNT(DISTINCT l.user_id) AS likes_count
                FROM films f
                JOIN films_directors fd ON f.id = fd.film_id
                LEFT JOIN directors d ON fd.director_id = d.id
                LEFT JOIN mpas m ON f.mpa_id = m.id
                LEFT JOIN films_genres fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                LEFT JOIN likes l ON f.id = l.film_id
                WHERE fd.director_id = ?
                GROUP BY f.id, m.name
                """ + orderBy;

        return findMany(sql, directorId);
    }
}