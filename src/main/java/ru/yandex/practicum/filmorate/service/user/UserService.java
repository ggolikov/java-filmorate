package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public UserDto getUser(int userId) {
        return userStorage.getUser(userId).map(UserMapper::mapToUserDto).orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void removeUser(int id) {
        userStorage.getUser(id).orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
        userStorage.removeUser(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public Collection<User> getUsers(Set<Integer> ids) {
        return userStorage.getUsers(ids);
    }

    public void addFriend(int id, int friendId) {
        if (id == friendId) {
            return;
        }

        UserDto optionalUser = getUser(id);

        if (optionalUser != null) {
            UserDto optionalFriend = getUser(friendId);
            if (optionalFriend != null) {
                Optional<Friendship> optionalFriendship = friendshipStorage.getFriendship(id, friendId);
                if (optionalFriendship.isEmpty()) {
                    friendshipStorage.addFriendship(id, friendId, "CONFIRMED");
                }
            }
        }
    }

    public void removeFriend(int id, int friendId) {
        Optional<User> optionalUser = userStorage.getUser(id);

        if (optionalUser.isPresent()) {
            Optional<User> optionalFriend = userStorage.getUser(friendId);

            if (optionalFriend.isPresent()) {
                friendshipStorage.removeFriendship(id, friendId);
            } else {
                throw new NotFoundException("Пользователь не найден");
            }
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public Collection<User> getFriends(int id) {
        userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(int id, int otherId) {
        Optional<User> optionalUser = userStorage.getUser(id);
        Optional<User> otherOptionalUser = userStorage.getUser(otherId);

        if (optionalUser.isPresent() && otherOptionalUser.isPresent()) {
            User user = optionalUser.get();
            User otherUser = otherOptionalUser.get();

            Set<Integer> user1Friends = user.getFriends().stream().map(User::getId).collect(Collectors.toSet());
            Set<Integer> user2Friends = otherUser.getFriends().stream().map(User::getId).collect(Collectors.toSet());

            Set<Integer> intersection = new HashSet<>(user1Friends);

            intersection.retainAll(user2Friends);

            if (intersection.isEmpty()) {
                return new ArrayList<>();
            }

            return new ArrayList<>(userStorage.getUsers(intersection));
        }

        return new ArrayList<>();
    }
}
