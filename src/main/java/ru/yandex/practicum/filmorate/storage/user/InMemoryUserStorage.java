package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage extends BaseUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    public User getUser(int id) {
        User user = users.get(id);

        if (user == null) {
            throw new NotFoundException("User with id " + id + " not found");
        }

        return user;
    }

    public User addUser(User user) {
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    public User updateUser(User user) {
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

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
