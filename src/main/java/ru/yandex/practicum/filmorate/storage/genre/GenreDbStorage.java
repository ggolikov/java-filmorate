package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("genreDbStorage")
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    private static final String GET_GENRE_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Genre> getGenre(int id) {
        return findOne(GET_GENRE_QUERY, id);
    }

    public Collection<Genre> getGenres() {
        return findMany(GET_ALL_GENRES_QUERY);
    }
}
