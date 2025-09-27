package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    public User getUser(int id) {
        User user = users.get(id);

        if (user == null) {
            throw new NotFoundException("User with id " + id + " not found");
        }

        return user;
    }

    public User addUser(@Valid User user) {
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    public User updateUser(@Valid User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id" + user.getId()  + "не найден");
        }

        validate(user);

        if (user.getName().isEmpty()) {
            user.setLogin(user.getLogin());
        }

        users.put(user.getId(), user);

        return user;
    }

    public void removeUser(int id) {
        if (!users.containsKey(id)) {
            return;
        }
        users.remove(id);
    }

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
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
