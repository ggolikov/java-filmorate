package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    public static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();

    public Film getFilm(int id) {
        Film film = films.get(id);

        if (film == null) {
            throw new NotFoundException("Film with id " + id + " not found");
        }

        return film;
    }

    public Film addFilm(@Valid Film film) {
        validate(film);

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(@Valid Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id" + film.getId()  + "не найден");
        }

        validate(film);

        films.put(film.getId(), film);
        return film;
    }

    public void removeFilm(int id) {
        if (!films.containsKey(id)) {
            return;
        }
        films.remove(id);
    }

    public Collection<Film> getFilms() {
        return films.values();
    }

    public void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Поле name не может быть пустым или null");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Длина поля description не должна превышать 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_FILM_DATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .max((a, b) -> b.compareTo(a))
                .orElse(0);
        return ++currentMaxId;
    }
}
