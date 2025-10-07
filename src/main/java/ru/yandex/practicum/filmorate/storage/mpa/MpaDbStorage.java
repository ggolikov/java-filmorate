package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {
    private static final String GET_RATING_QUERY = "SELECT * FROM ratings WHERE id = ?";
    private static final String GET_ALL_RATINGS_QUERY = "SELECT * FROM ratings";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper, ResultSetExtractor<List<Mpa>> extractor) {
        super(jdbc, mapper, extractor, Mpa.class);
    }

    public Optional<Mpa> getRating(int id) {
        return findOne(GET_RATING_QUERY, id);
    }

    public Collection<Mpa> getRatings() {
        return findMany(GET_ALL_RATINGS_QUERY);
    }
}
