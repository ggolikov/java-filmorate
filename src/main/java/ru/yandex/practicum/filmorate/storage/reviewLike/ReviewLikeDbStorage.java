package ru.yandex.practicum.filmorate.storage.reviewLike;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewLikeRowMapper;

import java.util.Optional;

@Repository
@Qualifier("reviewLikeDbStorage")
public class ReviewLikeDbStorage extends BaseStorage<ReviewLike> implements ReviewLikeStorage {
    private static final String GET_REVIEW_LIKE_QUERY = "SELECT * from reviews_likes WHERE review_id = ? AND user_id = ?";

    private static final String ADD_REVIEW_LIKE_QUERY = "INSERT INTO reviews_likes(review_id, user_id) VALUES (?, ?)";

    private static final String REMOVE_REVIEW_LIKE_QUERY = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?";

    public ReviewLikeDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new ReviewLikeRowMapper());
    }

    public Optional<ReviewLike> getLike(int reviewId, int userId) {
        return findOne(GET_REVIEW_LIKE_QUERY, reviewId, userId);
    }

    public void addLike(int reviewId, int userId) {
        update(ADD_REVIEW_LIKE_QUERY, reviewId, userId);
    }

    public void removeLike(int reviewId, int userId) {
        delete(REMOVE_REVIEW_LIKE_QUERY, reviewId, userId);
    }
}
