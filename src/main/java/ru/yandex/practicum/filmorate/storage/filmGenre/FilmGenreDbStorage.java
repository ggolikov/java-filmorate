package ru.yandex.practicum.filmorate.storage.filmGenre;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

@Repository
@Qualifier("filmGenreDbStorage")
public class FilmGenreDbStorage extends BaseStorage<FilmGenre> implements FilmGenreStorage {
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO films_genres(film_id, genre_id)" +
            "VALUES (?, ?)";

    public FilmGenreDbStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public void addFilmGenre(int filmId, int genreId) {
        update(
                INSERT_FILM_GENRE_QUERY,
                filmId,
                genreId
        );
    }
}
