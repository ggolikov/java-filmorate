package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.LikeRowMapper;

@Repository
@Qualifier("likeDbStorage")
public class LikeDbStorage extends BaseStorage<Like> implements LikeStorage {
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";

    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public LikeDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new LikeRowMapper());
    }

    public void addLike(int filmId, int userId) {
        update(
                ADD_LIKE_QUERY,
                filmId,
                userId
        );
    }

    public void removeLike(int filmId, int userId) {
        delete(REMOVE_LIKE_QUERY, filmId, userId);
    }
}
