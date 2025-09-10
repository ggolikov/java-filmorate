package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTests {
    User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setName("name");
        user.setEmail("user@user.com");
        user.setLogin("user");
        user.setName("name");
        user.setBirthday(new Date());
    }

    @Test
    public void emptyEmailTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setEmail("");

                    UserController.validate(user);
                }
        );

        assertEquals("Электронная почта не может быть пустой", exception.getMessage());
    }

    @Test
    public void missingAtEmailTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setEmail("user.com");

                    UserController.validate(user);
                }
        );

        assertEquals("Электронная почта должна содержать символ @", exception.getMessage());
    }

    @Test
    public void emptyLoginTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setLogin("");

                    UserController.validate(user);
                }
        );

        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    public void loginContainsSpacesTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setLogin("user user");

                    UserController.validate(user);
                }
        );

        assertEquals("Логин не может содержать пробелы", exception.getMessage());
    }

    @Test
    public void futureBirthDateTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setBirthday(new Date(2100, 1, 1));

                    UserController.validate(user);
                }
        );

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
