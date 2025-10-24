package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewDislike.ReviewDislikeStorage;
import ru.yandex.practicum.filmorate.storage.reviewLike.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
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
    private final FeedStorage feedStorage;

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

        Review addedReview = reviewStorage.addReview(review);

        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(addedReview.getId());
        event.setType(EventType.REVIEW);
        event.setOperation(Operation.ADD);
        event.setTimestamp(Instant.now().toEpochMilli());

        feedStorage.addEvent(event);

        return ReviewMapper.mapToReviewDto(addedReview);
    }

    public ReviewDto updateReview(ReviewDto dto) {
        Review existingReview = reviewStorage.getReview(dto.getReviewId()).orElseThrow(() -> new NotFoundException("Отзыв не найден с ID: " + dto.getReviewId()));

        Event event = new Event();
        event.setUserId(existingReview.getUserId());
        event.setEntityId(existingReview.getId());
        event.setType(EventType.REVIEW);
        event.setOperation(Operation.UPDATE);
        event.setTimestamp(Instant.now().toEpochMilli());

        feedStorage.addEvent(event);

        existingReview.setContent(dto.getContent());
        existingReview.setIsPositive(dto.getIsPositive());

        Review result = reviewStorage.updateReview(existingReview);
        return ReviewMapper.mapToReviewDto(result);
    }

    public void removeReview(int id) {
        Optional<Review> optionalReview = reviewStorage.getReview(id);

        if (optionalReview.isEmpty()) {
            return;
        }

        Review review = optionalReview.get();
        Integer userId = getReviewUserId(review);

        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(review.getId());
        event.setType(EventType.REVIEW);
        event.setOperation(Operation.REMOVE);
        event.setTimestamp(Instant.now().toEpochMilli());

        feedStorage.addEvent(event);
        reviewStorage.removeReview(id);
    }

    public void addLike(int id, int userId) {
        Optional<ReviewLike> reviewLike = reviewLikeStorage.getLike(id, userId);

        if (reviewLike.isEmpty()) {
            Event event = new Event();
            event.setUserId(userId);
            event.setEntityId(id);
            event.setType(EventType.LIKE);
            event.setOperation(Operation.ADD);
            event.setTimestamp(Instant.now().toEpochMilli());

            reviewLikeStorage.addLike(id, userId);

            updateReviewUsefulAfterLikesCountChange(id, userId, true);
        }
    }

    public void removeLike(int id, int userId) {
        Optional<ReviewLike> reviewLike = reviewLikeStorage.getLike(id, userId);

        if (reviewLike.isPresent()) {
            Event event = new Event();
            event.setUserId(userId);
            event.setEntityId(id);
            event.setType(EventType.LIKE);
            event.setOperation(Operation.REMOVE);
            event.setTimestamp(Instant.now().toEpochMilli());

            feedStorage.addEvent(event);
            reviewLikeStorage.removeLike(id, userId);

            updateReviewUsefulAfterLikesCountChange(id, userId, false);
        }
    }

    public void addDislike(int id, int userId) {
        Optional<ReviewLike> reviewDislike = reviewDislikeStorage.getDislike(id, userId);

        if (reviewDislike.isEmpty()) {
            reviewDislikeStorage.addDislike(id, userId);
            updateReviewUsefulAfterLikesCountChange(id, userId,false);
        }
    }

    public void removeDislike(int id, int userId) {
        Optional<ReviewLike> reviewDislike = reviewDislikeStorage.getDislike(id, userId);

        if (reviewDislike.isPresent()) {
            reviewDislikeStorage.removeDislike(id, userId);
            updateReviewUsefulAfterLikesCountChange(id, userId,true);
        }
    }

    private void updateReviewUsefulAfterLikesCountChange(int id, int userId, boolean increment) {
        ReviewDto dto = getReview(id);
        dto.setUserId(userId);
        dto.setUseful(increment ? dto.getUseful() + 1 : dto.getUseful() - 1);

        reviewStorage.updateReview(ReviewMapper.mapToReview(dto));

        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(dto.getReviewId());
        event.setType(EventType.REVIEW);
        event.setOperation(Operation.UPDATE);
        event.setTimestamp(Instant.now().toEpochMilli());
    }

    private int getReviewUserId(Review review) {
        List<Integer> users = userStorage.getUsers().stream().map(User::getId).toList();

        Integer userId = review.getUserId();

        if (userId != null) {
            if (!users.contains(userId)) {
                throw new NotFoundException("Пользователь с ID " + userId + " не найден");
            }
            return userId;
        }

        return -1;
    }
}
