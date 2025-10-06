package ru.yandex.practicum.filmorate.storage.film;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mappers.FilmResultExtractor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage extends BaseFilmStorage implements FilmStorage {
    private static final String GET_FILM_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT * FROM films";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ? WHERE id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE * FROM films WHERE id = ?";
    private final JdbcTemplate jdbc;

    public Film getFilm(int id) {
        return jdbc.queryForObject(GET_FILM_QUERY, Film.class, id);
    }

    public Film addFilm(Film film) {
        validate(film);

        int id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
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

        int rowsUpdated = jdbc.update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }

        return film;
    }

    public void removeFilm(int id) {
        jdbc.update(DELETE_FILM_QUERY, id);
    }

    public Collection<Film> getFilms() {
        return jdbc.query(GET_ALL_FILMS_QUERY, new FilmResultExtractor());
    }

    private int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;}, keyHolder);

        try {
            int id = keyHolder.getKeyAs(Integer.class);

            return id;
        } catch (NullPointerException e) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
