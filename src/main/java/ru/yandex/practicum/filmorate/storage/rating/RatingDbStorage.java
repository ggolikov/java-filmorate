package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.mappers.RatingResultExtractor;

import java.util.Collection;

@Repository
@Qualifier("ratingDbStorage")
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage {
    private static final String GET_RATING_QUERY = "SELECT * FROM ratings WHERE id = ?";
    private static final String GET_ALL_RATINGS_QUERY = "SELECT * FROM ratings";

    private final JdbcTemplate jdbc;

    public Rating getRating(int id) {
        return jdbc.queryForObject(GET_RATING_QUERY, Rating.class, id);
    }

    public Collection<Rating> getAllRatings() {
        return jdbc.query(GET_ALL_RATINGS_QUERY, new RatingResultExtractor());
    }
}
