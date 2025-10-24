package ru.yandex.practicum.filmorate.storage.mappers;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        Array friendsSqlArray = rs.getArray("friends");
        if (friendsSqlArray != null) {
            Object[] idsArray = (Object[]) friendsSqlArray.getArray();

            ArrayList<User> friends = new ArrayList<>();
            for (int i = 0; i < idsArray.length; i++) {
                if (idsArray[i] instanceof Integer) {
                    int id = (Integer) idsArray[i];
                    if (!friends.stream().map(User::getId).toList().contains(id)) {
                        User friend = new User();

                        friend.setId(id);

                        friends.add(friend);
                    }
                }
            }
            user.setFriends(friends);
        }
        return user;
    }
}