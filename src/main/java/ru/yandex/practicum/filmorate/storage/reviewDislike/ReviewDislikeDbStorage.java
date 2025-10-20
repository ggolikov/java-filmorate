package ru.yandex.practicum.filmorate.storage.reviewDislike;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewLikeRowMapper;

@Repository
@Qualifier("reviewDislikeDbStorage")
public class ReviewDislikeDbStorage extends BaseStorage<ReviewLike> implements ReviewDislikeStorage {
    private static final String ADD_REVIEW_DISLIKE_QUERY = "INSERT INTO reviews_dislikes(review_id, user_id) VALUES (?, ?)";

    private static final String REMOVE_REVIEW_DISLIKE_QUERY = "DELETE FROM reviews_dislikes WHERE review_id = ? AND user_id = ?";

    public ReviewDislikeDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new ReviewLikeRowMapper());
    }

    public void addDislike(int reviewId, int userId) {
        update(ADD_REVIEW_DISLIKE_QUERY, reviewId, userId);
    }

    public void removeDislike(int reviewId, int userId) {
        delete(REMOVE_REVIEW_DISLIKE_QUERY, reviewId, userId);
    }
}
