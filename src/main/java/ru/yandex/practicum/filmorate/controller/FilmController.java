package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/films")
public class FilmController {
    private final String defaultLikedFilmsCount = "10";

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilm(@PathVariable int filmId) {
        return filmService.getFilm(filmId);
    }

    @PostMapping
    public FilmDto addFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable Integer filmId) {
        filmService.removeFilm(filmId);
    }

    @GetMapping
    public Collection<FilmDto> getFilms() {
        return filmService.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(@RequestParam(value = "count", required = false,
            defaultValue = defaultLikedFilmsCount) Integer count,
                                               @RequestParam(required = false)
                                               @Positive(message = "Id жанра не может быть отрицательным") Integer genreId,
                                               @RequestParam(required = false)
                                                   @Positive(message = "Год не может быть отрицательным") Integer year) {
        return filmService.getMostLikedFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam Integer userId,
                                              @RequestParam Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFilmsByDirector(@PathVariable Integer directorId,
                                         @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public Collection<FilmDto> searchFilms(@RequestParam String query, @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }
}
