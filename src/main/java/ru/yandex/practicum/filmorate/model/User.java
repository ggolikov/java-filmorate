package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NotBlank(message = "Поле login не может быть пустым или null")
    private String login;

    @Email(message = "Указан некорректный формат почты")
    @NotBlank(message = "Поле email не может быть пустым или null")
    private String email;
    private String name;
    private LocalDate birthday;
}
