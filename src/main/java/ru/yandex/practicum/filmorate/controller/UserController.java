package ru.yandex.practicum.filmorate.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    public static void validate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getBirthday().after(new Date())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@RequestBody User user) {
        validate(user);

        if (user.getName().isEmpty()) {
            user.setLogin(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validate(user);

        if (user.getName().isEmpty()) {
            user.setLogin(user.getLogin());
        }

        return user;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .max((a, b) -> b.compareTo(a))
                .orElse(0);
        return ++currentMaxId;
    }
}
