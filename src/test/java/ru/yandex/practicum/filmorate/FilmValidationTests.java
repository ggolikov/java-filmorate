package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTests {
    Film film;

    @BeforeEach
    public void setup() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2024, 11, 11));
        film.setDuration(100);
    }

    @Test
    public void emptyNameTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setName("");

                    FilmController.validate(film);
                }
        );

        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    public void descriptionOverflowTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    String description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec qu1";
                    film.setDescription(description);

                    FilmController.validate(film);
                }
        );

        assertEquals("Описание фильма не может быть длиннее 200 символов", exception.getMessage());
    }

    @Test
    public void illegalDateTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setReleaseDate(LocalDate.of(1800, 1, 1));

                    FilmController.validate(film);
                }
        );

        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    public void negativeDurationTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setDuration(-100);

                    FilmController.validate(film);
                }
        );

        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }
}
