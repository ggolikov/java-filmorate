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

    private static final String GET_FILM_QUERY =
            """
            SELECT
                    f.*, m.name as mpa_name,
                    array_agg(fg.genre_id) as genre_ids,
                    array_agg(fg.GENRE_NAME) as genre_names,
                    count(l.user_id) as likes_count,
                    array_agg(fd.director_id) as director_ids,
                    array_agg(d.name) as director_names
                FROM films AS f
                LEFT JOIN likes AS l ON f.id = l.film_id
                LEFT JOIN (SELECT
                               _fg.GENRE_ID as genre_id,
                               _fg.FILM_ID as film_id,
                               _g.name AS genre_name
                                FROM FILMS_GENRES AS _fg
                                LEFT JOIN GENRES as _g ON _fg.GENRE_ID = _g.ID
                               ) AS fg
                    ON f.id = fg.film_id
                    LEFT JOIN mpas as m
                        on f.MPA_ID = m.ID
                    LEFT JOIN films_directors AS fd ON f.id = fd.film_id
                    LEFT JOIN directors AS d ON fd.director_id = d.id
                WHERE f.id = ?
                GROUP BY f.id;
            """;
    private static final String GET_ALL_FILMS_QUERY =
            """
            SELECT
                    f.*, m.name as mpa_name,
                    array_agg(fg.genre_id) as genre_ids,
                    array_agg(fg.GENRE_NAME) as genre_names,
                    count(l.user_id) as likes_count,
                    array_agg(fd.director_id) as director_ids,
                    array_agg(d.name) as director_names
                FROM films AS f
                LEFT JOIN likes AS l ON f.id = l.film_id
                LEFT JOIN (SELECT
                               _fg.GENRE_ID as genre_id,
                               _fg.FILM_ID as film_id,
                               _g.name AS genre_name
                                FROM FILMS_GENRES AS _fg
                                LEFT JOIN GENRES as _g ON _fg.GENRE_ID = _g.ID
                               ) AS fg
                    ON f.id = fg.film_id
                    LEFT JOIN mpas as m
                        on f.MPA_ID = m.ID
                    LEFT JOIN films_directors AS fd ON f.id = fd.film_id
                    LEFT JOIN directors AS d ON fd.director_id = d.id
                    GROUP BY f.id;
            """;
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String COMMON_FILMS_QUERY =
            """
                SELECT f.*, m.name as mpa_name,
                array_agg(fg.genre_id) as genre_ids,
                array_agg(g.name) as genre_names,
                COUNT(l.user_id) AS likes_count,
                array_agg(fd.director_id) as director_ids,
                array_agg(d.name) as director_names
                FROM films f
                JOIN likes l ON f.id = l.film_id
                JOIN mpas m ON f.mpa_id = m.id
                LEFT JOIN films_genres fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                LEFT JOIN films_directors AS fd ON f.id = fd.film_id
                LEFT JOIN directors AS d ON fd.director_id = d.id
                WHERE l.user_id = ? OR l.user_id = ?
                GROUP BY f.id
                HAVING COUNT(l.user_id) > 1
                ORDER BY likes_count DESC;
            """;

    public FilmDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new FilmRowMapper());
    }

    public Optional<Film> getFilm(int id) {
        return findOne(GET_FILM_QUERY, id);
    }

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

    public Film updateFilm(Film film) {
        getFilm(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id " + film.getId()  + " не найден"));

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

    public void removeFilm(int id) {
        getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
        delete(DELETE_FILM_QUERY, id);
    }

    public Collection<Film> getFilms() {
        return findMany(GET_ALL_FILMS_QUERY);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return findMany(COMMON_FILMS_QUERY, userId, friendId);
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        String orderBy = "year".equals(sortBy) ? "f.release_date" : "likes_count";

        String sql = """
            SELECT
                f.*, m.name as mpa_name,
                array_agg(fg.genre_id) as genre_ids,
                array_agg(g.name) as genre_names,
                count(l.user_id) as likes_count,
                array_agg(fd.director_id) as director_ids,
                array_agg(d.name) as director_names
            FROM films AS f
            LEFT JOIN likes AS l ON f.id = l.film_id
            LEFT JOIN films_genres AS fg ON f.id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.id
            LEFT JOIN mpas as m on f.MPA_ID = m.ID
            LEFT JOIN films_directors AS fd ON f.id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.id
            WHERE fd.director_id = ?
            GROUP BY f.id
            ORDER BY %s;
            """.formatted(orderBy);
        return findMany(sql, directorId);
    }

    @Override
    public Collection<Film> getMostLikedFilms(int count, Integer genreId, Integer year) {
        String sql =
                """
                SELECT
                        f.*, m.name as mpa_name,
                        array_agg(fg.genre_id) as genre_ids,
                        array_agg(g.name) as genre_names,
                        count(l.user_id) as likes_count,
                        array_agg(fd.director_id) as director_ids,
                        array_agg(d.name) as director_names
                    FROM films AS f
                    LEFT JOIN likes AS l ON f.id = l.film_id
                    LEFT JOIN films_genres AS fg ON f.id = fg.film_id
                    LEFT JOIN genres AS g ON fg.genre_id = g.id
                    LEFT JOIN mpas as m
                            on f.MPA_ID = m.ID
                    LEFT JOIN films_directors AS fd ON f.id = fd.film_id
                    LEFT JOIN directors AS d ON fd.director_id = d.id
                    %s
                    GROUP BY f.id
                    ORDER BY likes_count DESC
                    LIMIT ?;
                """;

        String whereClause = "";
        if (genreId != null && year != null) {
            whereClause = "WHERE g.id = " + genreId + " AND EXTRACT(YEAR FROM f.release_date) = " + year;
        } else if (genreId != null) {
            whereClause = "WHERE g.id = " + genreId;
        } else if (year != null) {
            whereClause = "WHERE EXTRACT(YEAR FROM f.release_date) = " + year;
        }

        return findMany(String.format(sql, whereClause), count);
    }

    @Override
    public Collection<Film> searchFilms(String query, String by) {
        String[] searchBy = by.split(",");
        String whereClause;
        if (searchBy.length == 2) {
            whereClause = "LOWER(d.name) LIKE LOWER('%" + query + "%') OR " + "LOWER(f.name) LIKE LOWER('%" + query + "%')";
        } else if (searchBy[0].equals("director")) {
            whereClause = "LOWER(d.name) LIKE LOWER('%" + query + "%')";
        } else {
            whereClause = "LOWER(f.name) LIKE LOWER('%" + query + "%')";
        }

        String sql = """
            SELECT
                f.*, m.name as mpa_name,
                array_agg(fg.genre_id) as genre_ids,
                array_agg(g.name) as genre_names,
                count(l.user_id) as likes_count,
                array_agg(fd.director_id) as director_ids,
                array_agg(d.name) as director_names
            FROM films AS f
            LEFT JOIN likes AS l ON f.id = l.film_id
            LEFT JOIN films_genres AS fg ON f.id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.id
            LEFT JOIN mpas as m on f.MPA_ID = m.ID
            LEFT JOIN films_directors AS fd ON f.id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.id
            WHERE %s
            GROUP BY f.id
            ORDER BY likes_count DESC;
            """.formatted(whereClause);
        return findMany(sql);
    }

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
}