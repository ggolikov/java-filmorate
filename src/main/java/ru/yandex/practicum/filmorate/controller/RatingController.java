package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.rating.RatingService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable int id) {
        try {
            return ratingService.getRating(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping
    public Collection<Rating> getRatings() {
        return ratingService.getRatings();
    }
}
