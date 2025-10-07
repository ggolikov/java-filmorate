package ru.yandex.practicum.filmorate.storage.mappers;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setFollowedUserId(rs.getInt("followed_user_id"));
        friendship.setFollowingUserId(rs.getInt("following_user_id"));
        friendship.setRelationStatus(rs.getString("status"));

        return friendship;
    }
}