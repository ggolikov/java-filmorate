package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewDislike.ReviewDislikeStorage;
import ru.yandex.practicum.filmorate.storage.reviewLike.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final ReviewDislikeStorage reviewDislikeStorage;

    public ReviewDto getReview(int id) {
        return reviewStorage.getReview(id)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден с ID: " + id));
    }

    public Collection<ReviewDto> getReviews(int filmId, int count) {
        return reviewStorage.getReviews(filmId, count).stream().map(ReviewMapper::mapToReviewDto).collect(Collectors.toList());
    }

    public ReviewDto addReview(ReviewDto dto) {
        Review review = ReviewMapper.mapToReview(dto);
        List<Integer> users = userStorage.getUsers().stream().map(User::getId).toList();
        List<Integer> films = filmStorage.getFilms().stream().map(Film::getId).toList();
        Integer userId = review.getUserId();
        Integer filmId = review.getFilmId();

        if (userId != null && filmId != null) {
            if (!users.contains(userId)) {
                throw new NotFoundException("Пользователь с ID " + userId + " не найден");
            }
            if (!films.contains(filmId)) {
                throw new NotFoundException("Фильм с ID " + filmId + " не найден");
            }
        }

        return ReviewMapper.mapToReviewDto(reviewStorage.addReview(review));
    }

    public ReviewDto updateReview(ReviewDto dto) {
        Review review = ReviewMapper.mapToReview(dto);
        return ReviewMapper.mapToReviewDto(reviewStorage.updateReview(review));
    }

    public void removeReview(int id) {
        reviewStorage.removeReview(id);
    }

    public void addLike(int id, int userId) {
        Optional<ReviewLike> reviewLike = reviewLikeStorage.getLike(id, userId);
        Optional<ReviewLike> reviewDislike = reviewDislikeStorage.getDislike(id, userId);

        if (reviewLike.isEmpty()) {
            if (reviewDislike.isPresent()) {
                removeDislike(id, userId);
            }
            reviewLikeStorage.addLike(id, userId);
            updateReviewUsefulAfterLikesCountChange(id, true);
        }
    }

    public void removeLike(int id, int userId) {
        Optional<ReviewLike> reviewLike = reviewLikeStorage.getLike(id, userId);

        if (reviewLike.isPresent()) {
            reviewLikeStorage.removeLike(id, userId);
            updateReviewUsefulAfterLikesCountChange(id, false);
        }
    }

    public void addDislike(int id, int userId) {
        Optional<ReviewLike> reviewDislike = reviewDislikeStorage.getDislike(id, userId);
        Optional<ReviewLike> reviewLike = reviewLikeStorage.getLike(id, userId);

        if (reviewDislike.isEmpty()) {
            if (reviewLike.isPresent()) {
                removeLike(id, userId);
            }
            reviewDislikeStorage.addDislike(id, userId);
            updateReviewUsefulAfterLikesCountChange(id, false);
        }
    }

    public void removeDislike(int id, int userId) {
        Optional<ReviewLike> reviewDislike = reviewDislikeStorage.getDislike(id, userId);

        if (reviewDislike.isPresent()) {
            reviewDislikeStorage.removeDislike(id, userId);
            updateReviewUsefulAfterLikesCountChange(id, true);
        }
    }

    private void updateReviewUsefulAfterLikesCountChange(int id, boolean increment) {
        ReviewDto dto = getReview(id);

        dto.setUseful(increment ? dto.getUseful() + 1 : dto.getUseful() - 1);

        updateReview(dto);
    }
}
