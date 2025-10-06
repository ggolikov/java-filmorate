package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage extends BaseFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    public Film getFilm(int id) {
        Film film = films.get(id);

        if (film == null) {
            throw new NotFoundException("Film with id " + id + " not found");
        }

        return film;
    }

    public Film addFilm(Film film) {
        validate(film);

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
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

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
