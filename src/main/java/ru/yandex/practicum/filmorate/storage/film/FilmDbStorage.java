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
                    count(l.user_id) as likes_count
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
                WHERE f.id = ?
                GROUP BY f.id;
            """;
    private static final String GET_ALL_FILMS_QUERY =
            """
            SELECT
                    f.*, m.name as mpa_name,
                    array_agg(fg.genre_id) as genre_ids,
                    array_agg(fg.GENRE_NAME) as genre_names,
                    count(l.user_id) as likes_count
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
                    GROUP BY f.id;
            """;
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";

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
        List<Integer> ids = getFilms().stream().mapToInt(Film::getId).boxed().toList();

        if (!ids.contains(film.getId())) {
            throw new NotFoundException("Фильм с id" + film.getId()  + "не найден");
        }

        validate(film);

        update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        return film;
    }

    public void removeFilm(int id) {
        update(DELETE_FILM_QUERY, id);
    }

    public Collection<Film> getFilms() {
        return findMany(GET_ALL_FILMS_QUERY);
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
