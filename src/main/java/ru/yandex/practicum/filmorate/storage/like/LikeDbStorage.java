package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.LikeRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("likeDbStorage")
public class LikeDbStorage extends BaseStorage<Like> implements LikeStorage {
    private static final String GET_LIKE_QUERY = "SELECT * from likes WHERE film_id = ? AND user_id = ?";

    private static final String ADD_LIKE_QUERY = "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";

    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String FIND_MOST_SIMILAR_USER_QUERY = """
            SELECT l2.user_id
            FROM likes l1
            JOIN likes l2 ON l1.film_id = l2.film_id
            WHERE l1.user_id = ? AND l2.user_id != ?
            GROUP BY l2.user_id
            ORDER BY COUNT(*) DESC
            LIMIT 1
            """;

    private static final String GET_RECOMMENDED_FILMS_QUERY = """
            SELECT
                f.*,
                m.name AS mpa_name,
                array_agg(DISTINCT g.id ORDER BY g.id) AS genre_ids,
                array_agg(DISTINCT g.name ORDER BY g.id) AS genre_names,
                array_agg(DISTINCT d.id ORDER BY d.id) AS director_ids,
                array_agg(DISTINCT d.name ORDER BY d.id) AS director_names,
                COUNT(DISTINCT l.user_id) AS likes_count
            FROM films f
            JOIN likes l_similar ON f.id = l_similar.film_id
            LEFT JOIN mpas m ON f.mpa_id = m.id
            LEFT JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN films_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE l_similar.user_id = ?
            AND f.id NOT IN (
                SELECT film_id FROM likes WHERE user_id = ?
            )
            GROUP BY f.id, m.name
            ORDER BY likes_count DESC
            """;

    private final FilmRowMapper filmRowMapper;

    public LikeDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        super(jdbc, new LikeRowMapper());
        this.filmRowMapper = filmRowMapper;
    }

    public Optional<Like> getLike(int filmId, int usrId) {
        return findOne(GET_LIKE_QUERY, filmId, usrId);
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

    @Override
    public Optional<Integer> findMostSimilarUser(int userId) {
        try {
            Integer similarUserId = jdbc.queryForObject(
                    FIND_MOST_SIMILAR_USER_QUERY,
                    Integer.class,
                    userId,
                    userId
            );
            return Optional.ofNullable(similarUserId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getRecommendedFilms(int userId, int similarUserId) {
        List<Film> films = jdbc.query(
                GET_RECOMMENDED_FILMS_QUERY,
                filmRowMapper,
                similarUserId,
                userId
        );
        return films;
    }
}