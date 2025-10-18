package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Director {
    private int id;
    @NotBlank(message = "Поле name не может быть пустым или null")
    private String name;
}
