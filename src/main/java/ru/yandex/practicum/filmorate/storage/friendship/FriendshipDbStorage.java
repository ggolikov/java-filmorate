package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("friendshipDbStorage")
public class FriendshipDbStorage extends BaseStorage<Friendship> implements FriendshipStorage {
    private static final String GET_FRIENDSHIP_QUERY = "SELECT * FROM friendship WHERE following_user_id = ? AND followed_user_id = ?";

    private static final String INSERT_FRIENDSHIP_QUERY = "INSERT INTO friendship(following_user_id, followed_user_id, status) VALUES (?, ?, ?)";

    private static final String DELETE_FRIENDSHIP_QUERY = "DELETE FROM friendship WHERE following_user_id = ? AND followed_user_id = ?";

    public FriendshipDbStorage(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Friendship> getFriendship(int followingUserId, int followedUserId) {
        return findOne(GET_FRIENDSHIP_QUERY, followingUserId, followedUserId);
    }

    public void addFriendship(int followingUserId, int followedUserId, String status) {
        update(
                INSERT_FRIENDSHIP_QUERY,
                followingUserId,
                followedUserId,
                status
        );
    }

    public void removeFriendship(int id, int friendId) {
        delete(DELETE_FRIENDSHIP_QUERY, id, friendId);
    }

}
