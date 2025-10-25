package ru.yandex.practicum.filmorate.storage.filmDirector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.List;

@Repository
@Qualifier("filmDirectorDbStorage")
public class FilmDirectorDbStorage extends BaseStorage<Director> implements FilmDirectorStorage {

    public FilmDirectorDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new DirectorRowMapper());
    }

    private static final String INSERT_FILM_DIRECTORS_QUERY =
            "INSERT INTO films_directors (film_id, director_id)";

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

    @Override
    public void addFilmDirectors(int filmId, List<Integer> directorIds) {
        StringBuilder valuesClause = new StringBuilder(" VALUES ");
        for (int i = 0; i < directorIds.size(); i++) {
            valuesClause.append("(" + filmId + ", " + directorIds.get(i) + ")");
            if (i < directorIds.size() - 1) {
                valuesClause.append(", ");
            }
        }

        jdbc.update(INSERT_FILM_DIRECTORS_QUERY + valuesClause);
    }

    @Override
    public void deleteDirectorsByFilmId(int filmId) {
        jdbc.update(DELETE_BY_FILM_QUERY, filmId);
    }

    @Override
    public List<Director> getDirectorsByFilmId(int filmId) {
        return jdbc.query(SELECT_DIRECTORS_BY_FILM_QUERY, directorRowMapper, filmId);
    }
}
