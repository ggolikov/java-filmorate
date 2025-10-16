package ru.yandex.practicum.filmorate.storage.filmDirector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Repository
@Qualifier("filmDirectorDbStorage")
public class FilmDirectorDbStorage implements FilmDirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_FILM_DIRECTOR_QUERY =
            "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";

    private static final String DELETE_BY_FILM_QUERY =
            "DELETE FROM films_directors WHERE film_id = ?";

    private static final String SELECT_DIRECTORS_BY_FILM_QUERY = """
            SELECT d.id, d.name
            FROM directors d
            JOIN films_directors fd ON d.id = fd.director_id
            WHERE fd.film_id = ?
            """;

    private final RowMapper<Director> directorRowMapper = (rs, rowNum) -> {
        Director director = new Director();
        director.setId(rs.getInt("id"));
        director.setName(rs.getString("name"));
        return director;
    };

    public FilmDirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilmDirector(int filmId, int directorId) {
        jdbcTemplate.update(INSERT_FILM_DIRECTOR_QUERY, filmId, directorId);
    }

    @Override
    public void deleteDirectorsByFilmId(int filmId) {
        jdbcTemplate.update(DELETE_BY_FILM_QUERY, filmId);
    }

    @Override
    public List<Director> getDirectorsByFilmId(int filmId) {
        return jdbcTemplate.query(SELECT_DIRECTORS_BY_FILM_QUERY, directorRowMapper, filmId);
    }
}
