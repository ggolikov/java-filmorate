package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final String GET_USER_QUERY =
            """
                        SELECT
                            u.*,
                            array_agg(f.followed_user_id) as friends
                        FROM users AS u
                            LEFT JOIN FRIENDSHIP as f
                            on f.FOLLOWING_USER_ID = u.ID
                        WHERE u.id = ?
                        GROUP BY u.id;
                    """;
    private static final String GET_ALL_USERS_QUERY =
            """
                        SELECT
                            u.*,
                            array_agg(f.followed_user_id) as friends
                        FROM users AS u
                                 LEFT JOIN FRIENDSHIP as f
                                           on f.FOLLOWING_USER_ID = u.ID
                    """;
    private static final String USERS_GROUPING = " GROUP BY u.id;";

    private static final String INSERT_USER_QUERY = "INSERT INTO users(login, email, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE id = ?";

    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id IN (?)";

    private static final String USER_FRIENDS_QUERY =
            """
                            SELECT u.*, array_agg(f.followed_user_id) AS friends
                            FROM users u
                            JOIN friendship f1 ON u.id = f1.followed_user_id
                            LEFT JOIN friendship f ON f.following_user_id = u.id
                            WHERE f1.following_user_id = ?
                            GROUP BY u.id;
                    """;

    public UserDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new UserRowMapper());
    }

    public Optional<User> getUser(int id) {
        return findOne(GET_USER_QUERY, id);
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
            throw new NotFoundException("Пользователь с id" + user.getId() + "не найден");
        }

        validate(user);

        if (user.getName().isEmpty()) {
            user.setLogin(user.getLogin());
        }

        update(UPDATE_USER_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    public void removeUser(int id) {
        delete(DELETE_USER_QUERY, id);
    }

    public Collection<User> getUsers() {
        return findMany(GET_ALL_USERS_QUERY + USERS_GROUPING);
    }

    public Collection<User> getUsers(Set<Integer> ids) {
        int[] idsArray = ids.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        StringBuilder whereClause = new StringBuilder(" WHERE id in (");
        for (int i = 0; i < idsArray.length; i++) {
            whereClause.append(idsArray[i]);
            if (i < ids.size() - 1) {
                whereClause.append(", ");
            }
        }

        whereClause.append(")");

        return findMany(GET_ALL_USERS_QUERY + whereClause + USERS_GROUPING);
    }

    public Collection<User> getFriends(int id) {
        return findMany(USER_FRIENDS_QUERY, id);
    }

    public void validate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Поле email не может быть пустым или null");
        }

        if (!user.getEmail().contains("@") || user.getEmail().startsWith("@") || user.getEmail().endsWith("@")) {
            throw new ValidationException("Указан некорректный формат почты");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Поле login не может быть пустым или null");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Поле login не может содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
