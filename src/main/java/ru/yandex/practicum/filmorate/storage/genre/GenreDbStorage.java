package ru.yandex.practicum.filmorate.storage.genre;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreResultExtractor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("genreDbStorage")
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    private static final String GET_GENRE_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper, ResultSetExtractor<List<Genre>> extractor) {
        super(jdbc, mapper, extractor, Genre.class);
    }

    public Optional<Genre> getGenre(int id) {
        return findOne(GET_GENRE_QUERY, id);
    }

    public Collection<Genre> getGenres() {
        return findMany(GET_ALL_GENRES_QUERY);
    }
}
