package ru.yandex.practicum.filmorate.storage.filmGenre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmGenreRowMapper;

import java.util.List;

@Repository
@Qualifier("filmGenreDbStorage")
public class FilmGenreDbStorage extends BaseStorage<FilmGenre> implements FilmGenreStorage {
    private static final String INSERT_FILM_GENRES_QUERY = "INSERT INTO films_genres";

    private static final String DELETE_GENRES_BY_FILM_QUERY = "DELETE FROM films_genres WHERE film_id = ?";

    public FilmGenreDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new FilmGenreRowMapper());
    }

    public void addFilmGenres(int filmId, List<Integer> genreIds) {
        StringBuilder valuesClause = new StringBuilder(" VALUES ");
        for (int i = 0; i < genreIds.size(); i++) {
            valuesClause.append("(" + filmId + ", " + genreIds.get(i) + ")");
            if (i < genreIds.size() - 1) {
                valuesClause.append(", ");
            }
        }

        jdbc.update(INSERT_FILM_GENRES_QUERY + valuesClause);
    }

    public void deleteGenresByFilmId(int filmId) {
        update(DELETE_GENRES_BY_FILM_QUERY, filmId);
    }
}
