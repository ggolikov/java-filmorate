package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Review {
    private Integer id;
    private Integer userId;
    private Integer filmId;
    private String content;
    private Boolean isPositive;
    private Integer useful = 0;
}
