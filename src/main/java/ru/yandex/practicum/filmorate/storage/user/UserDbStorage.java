package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final String GET_USER_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String GET_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String INSERT_USER_QUERY = "INSERT INTO users(login, email, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String INSERT_FRIENDSHIP_QUERY = "INSERT INTO friendship(following_user_id, followed_user_id, status)" +
            "VALUES (?, ?, ?)";
    private static final String USER_FRIENDS_QUERY = "SELECT" +
            "    ID,\n" +
            "    LOGIN,\n" +
            "    EMAIL,\n" +
            "    NAME,\n" +
            "    BIRTHDAY\n" +
            "FROM USERS\n" +
            "WHERE ID IN (\n" +
            "SELECT DISTINCT\n" +
            "    FOLLOWED_USER_ID\n" +
            "    FROM FRIENDSHIP AS F\n" +
            "    LEFT JOIN USERS AS U ON F.FOLLOWING_USER_ID = U.ID\n" +
            "\n" +
            "WHERE U.ID = ?\n" +
            ");";

    private static final String DELETE_USER_FRIEND_QUERY = "DELETE FROM friendship WHERE following_user_id = ? AND followed_user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
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
           throw new NotFoundException("Пользователь с id" + user.getId()  + "не найден");
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
        return findMany(GET_ALL_USERS_QUERY);
    }

    public void addFriend(int id1, int id2, String status) {
        update(
                INSERT_FRIENDSHIP_QUERY,
                id1,
                id2,
                status
        );
    }

    public void removeFriend(int id, int friendId) {
        delete(DELETE_USER_FRIEND_QUERY, id, friendId);
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
