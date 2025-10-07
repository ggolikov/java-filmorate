package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Поле name не может быть пустым или null")
    private String name;
    @Size(min = 0, max = 200, message = "Длина поля description не должна превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Min(value = 0, message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    private List<Genre> genres;
    private Mpa mpa;
}