package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User getUser(int id);

    User addUser(@Valid User user);

    User updateUser(@Valid User user);

    void removeUser(int id);

    Collection<User> getUsers();

    void validate(User user) throws ValidationException;
}
