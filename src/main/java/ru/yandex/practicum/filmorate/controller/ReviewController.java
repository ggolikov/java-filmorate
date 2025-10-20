package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final String defaultFilmReviewsCount = "10";
    private final ReviewService reviewService;

    @GetMapping
    public Collection<ReviewDto> getReviews(@RequestParam(value = "filmId", required = false, defaultValue = "-1") int filmId, @RequestParam(value = "count", required = false,
            defaultValue = defaultFilmReviewsCount) int count) {
        return reviewService.getReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable int id) {
        return reviewService.getReview(id);
    }

    @PostMapping
    public ReviewDto addReview(@RequestBody ReviewDto dto) {
        return reviewService.addReview(dto);
    }

    @PutMapping
    public ReviewDto updateReview(@RequestBody ReviewDto dto) {
        return reviewService.updateReview(dto);
    }

    @DeleteMapping("/{id}")
    public void removeReview(@PathVariable int id) {
        reviewService.removeReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeDislike(id, userId);
    }

}
