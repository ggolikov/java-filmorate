package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public abstract class BaseUserStorage implements UserStorage {
    public void validate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Поле email не может быть пустым или null");
        }

        if (!user.getEmail().contains("@") || user.getEmail().startsWith("@") || user.getEmail().endsWith("@")) {
            throw new ValidationException("Указан некорректный формат почты");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Поле login не может быть пустым или null");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Поле login не может содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
