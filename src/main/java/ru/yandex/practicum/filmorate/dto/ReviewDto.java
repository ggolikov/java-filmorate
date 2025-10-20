package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Integer reviewId;
    private Integer userId;
    private Integer filmId;
    private String content;
    private Boolean isPositive;
    private Integer useful;
}
