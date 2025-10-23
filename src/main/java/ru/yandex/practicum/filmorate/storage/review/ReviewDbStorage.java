package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("reviewDbStorage")
public class ReviewDbStorage extends BaseStorage<Review> implements ReviewStorage {
    private static final String GET_REVIEW_QUERY = "SELECT * from reviews WHERE id = ?";
    private static final String GET_ALL_REVIEWS_QUERY = "SELECT * from reviews";
    private static final String GET_ALL_REVIEWS_QUERY_ORDER_CLAUSE = " ORDER BY useful DESC";

    private static final String INSERT_REVIEW_QUERY = "INSERT INTO reviews(user_id, film_id, content, is_positive, useful)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE id = ?";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE id IN (?)";

    public ReviewDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new ReviewRowMapper());
    }

    public Optional<Review> getReview(int id) {
        return findOne(GET_REVIEW_QUERY, id);
    }

    public Review addReview(Review review) {
        validate(review);

        int id = insert(
                INSERT_REVIEW_QUERY,
                review.getUserId(),
                review.getFilmId(),
                review.getContent(),
                review.getIsPositive(),
                review.getUseful()
        );
        review.setId(id);
        return review;
    }

    public Review updateReview(Review review) {
        if (getReview(review.getId()).isEmpty()) {
            throw new NotFoundException("Отзыв с id " + review.getId() + " не найден");
        }

        validate(review);

        update(
                UPDATE_REVIEW_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getId()
        );
        return getReview(review.getId()).get();
    }

    public void removeReview(int id) {
        delete(DELETE_REVIEW_QUERY, id);
    }

    public Collection<Review> getReviews() {
        return findMany(GET_ALL_REVIEWS_QUERY + GET_ALL_REVIEWS_QUERY_ORDER_CLAUSE);
    }

    public Collection<Review> getReviews(int filmId, int count) {
        StringBuilder sql = new StringBuilder(GET_ALL_REVIEWS_QUERY);
        List<Object> params = new ArrayList<>();

        if (filmId != -1) {
            sql.append(" WHERE film_id = ?");
            params.add(filmId);
        }

        sql.append(GET_ALL_REVIEWS_QUERY_ORDER_CLAUSE);

        sql.append(" LIMIT ?");
        params.add(count);

        return findMany(sql.toString(), params.toArray());
    }

    public void validate(Review review) throws ValidationException {
        if (review.getContent() == null) {
            throw new ValidationException("Поле content не может быть null");
        }

        if (review.getUserId() == null) {
            throw new ValidationException("Поле userId не может быть пустым или null");
        }

        if (review.getFilmId() == null) {
            throw new ValidationException("Поле filmId не может быть пустым или null");
        }

        if (review.getIsPositive() == null) {
            throw new ValidationException("Поле isPositive не может быть пустым или null");
        }
    }
}