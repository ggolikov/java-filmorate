package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id" + user.getId()  + "не найден");
        }

        validate(user);

        if (user.getName().isEmpty()) {
            user.setLogin(user.getLogin());
        }

        users.put(user.getId(), user);

        return user;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

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

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .max((a, b) -> b.compareTo(a))
                .orElse(0);
        return ++currentMaxId;
    }
}
