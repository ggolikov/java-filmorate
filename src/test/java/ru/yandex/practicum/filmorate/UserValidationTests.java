package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTests {
    UserController userController  = new UserController();
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

                    userController.validate(user);
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

                    userController.validate(user);
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

                    userController.validate(user);
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

                    userController.validate(user);
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

                    userController.validate(user);
                }
        );

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
