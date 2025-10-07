package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> getUser(int id);

    User addUser(@Valid User user);

    User updateUser(@Valid User user);

    void removeUser(int id);

    Collection<User> getUsers();

    Collection<User> getFriends(int id);

    void validate(User user) throws ValidationException;
}
