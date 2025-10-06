package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserResultExtractor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage extends BaseUserStorage implements UserStorage {
    private static final String GET_USER_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String GET_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String INSERT_USER_QUERY = "INSERT INTO users(login, email, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE * FROM users WHERE id = ?";

    private final JdbcTemplate jdbc;

    public User getUser(int id) {
        return jdbc.queryForObject(GET_USER_QUERY, User.class, id);
    }

    public User addUser(User user) {
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        int id = insert(
                INSERT_USER_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User updateUser(User user) {
       List<Integer> ids = getUsers().stream().mapToInt(User::getId).boxed().toList();

       if (!ids.contains(user.getId())) {
           throw new NotFoundException("Пользователь с id" + user.getId()  + "не найден");
       }

        validate(user);

        if (user.getName().isEmpty()) {
            user.setLogin(user.getLogin());
        }

        int rowsUpdated = jdbc.update(UPDATE_USER_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }

        return user;
    }

    public void removeUser(int id) {
        jdbc.update(DELETE_USER_QUERY, id);
    }

    public Collection<User> getUsers() {
        return jdbc.query(GET_ALL_USERS_QUERY, new UserResultExtractor());
    }

    private int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;}, keyHolder);

        try {
            int id = keyHolder.getKeyAs(Integer.class);

            return id;
        } catch (NullPointerException e) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
