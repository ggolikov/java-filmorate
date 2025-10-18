package ru.yandex.practicum.filmorate.storage.filmGenre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmGenreRowMapper;

@Repository
@Qualifier("filmGenreDbStorage")
public class FilmGenreDbStorage extends BaseStorage<FilmGenre> implements FilmGenreStorage {
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO films_genres(film_id, genre_id)" +
            "VALUES (?, ?)";

    private static final String DELETE_GENRES_BY_FILM_QUERY = "DELETE FROM films_genres WHERE film_id = ?";

    public FilmGenreDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new FilmGenreRowMapper());
    }

    public void addFilmGenre(int filmId, int genreId) {
        update(
                INSERT_FILM_GENRE_QUERY,
                filmId,
                genreId
        );
    }

    public void deleteGenresByFilmId(int filmId) {
        update(DELETE_GENRES_BY_FILM_QUERY, filmId);
    }
}
