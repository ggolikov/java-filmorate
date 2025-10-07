package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {
    private static final String GET_RATING_QUERY = "SELECT * FROM mpas WHERE id = ?";
    private static final String GET_ALL_RATINGS_QUERY = "SELECT * FROM mpas";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Mpa> getRating(int id) {
        return findOne(GET_RATING_QUERY, id);
    }

    public Collection<Mpa> getRatings() {
        return findMany(GET_ALL_RATINGS_QUERY);
    }
}
