package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @PostMapping()
    public Genre addGenre(@RequestBody @Valid Genre genre) {
        return genreService.addGenre(genre);
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) {
        try {
            return genreService.getGenre(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping
    public Collection<Genre> getGenres() {
        return genreService.getGenres();
    }
}
