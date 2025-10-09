package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTests {
    FilmStorage filmStorage = new FilmDbStorage(new JdbcTemplate());
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

                    filmStorage.validate(film);
                }
        );

        assertEquals("Поле name не может быть пустым или null", exception.getMessage());
    }

    @Test
    public void descriptionOverflowTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    String description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec qu1";
                    film.setDescription(description);

                    filmStorage.validate(film);
                }
        );

        assertEquals("Длина поля description не должна превышать 200 символов", exception.getMessage());
    }

    @Test
    public void illegalDateTest() throws ValidationException {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setReleaseDate(LocalDate.of(1800, 1, 1));

                    filmStorage.validate(film);
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

                    filmStorage.validate(film);
                }
        );

        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }
}
