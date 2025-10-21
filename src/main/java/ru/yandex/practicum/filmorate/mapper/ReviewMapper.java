package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReviewMapper {
    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUseful(review.getUseful());
        return dto;
    }

    public static Review mapToReview(ReviewDto dto) {
        Review review = new Review();
        review.setId(dto.getReviewId());
        review.setUserId(dto.getUserId());
        review.setFilmId(dto.getFilmId());
        review.setContent(dto.getContent());
        review.setIsPositive(dto.getIsPositive());
        review.setUseful(Objects.requireNonNullElse(dto.getUseful(), 0));
        return review;
    }
}
