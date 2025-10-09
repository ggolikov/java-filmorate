package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTests {
    UserStorage userStorage  = new UserDbStorage(new JdbcTemplate());
    User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setName("name");
        user.setEmail("user@user.com");
        user.setLogin("user");
        user.setName("name");
        user.setBirthday(LocalDate.now());
    }

    @Test
    public void emptyEmailTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setEmail("");

                    userStorage.validate(user);
                }
        );

        assertEquals("Поле email не может быть пустым или null", exception.getMessage());
    }

    @Test
    public void missingAtEmailTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setEmail("user.com");

                    userStorage.validate(user);
                }
        );

        assertEquals("Указан некорректный формат почты", exception.getMessage());
    }

    @Test
    public void emptyLoginTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setLogin("");

                    userStorage.validate(user);
                }
        );

        assertEquals("Поле login не может быть пустым или null", exception.getMessage());
    }

    @Test
    public void loginContainsSpacesTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setLogin("user user");

                    userStorage.validate(user);
                }
        );

        assertEquals("Поле login не может содержать пробелы", exception.getMessage());
    }

    @Test
    public void futureBirthDateTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setBirthday(LocalDate.of(2100, 1, 1));

                    userStorage.validate(user);
                }
        );

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
