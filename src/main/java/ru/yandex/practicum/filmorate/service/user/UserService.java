package ru.yandex.practicum.filmorate.service.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(int userId) {
        return userStorage.getUser(userId);
    }

    public User addUser(@Valid User user) {
        return userStorage.addUser(user);
    };

    public User updateUser(@Valid User user) {
        return userStorage.updateUser(user);
    }

    public void removeUser(int id) {
        userStorage.removeUser(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.getUser(id);

        if (user != null) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                Set<Integer> userFriends = user.getFriends();
                userFriends.add(friendId);
                // Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
                Set<Integer> friendFriends = friend.getFriends();
                friendFriends.add(id);
            }
        }
    }

    public void removeFriend(int id, int friendId) {
        User user = userStorage.getUser(id);

        if (user != null) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                Set<Integer> userFriends = user.getFriends();
                userFriends.remove(friendId);
            }
        }
    }

    public Collection<User> getFriends(int id) {
        User user = userStorage.getUser(id);

        if (user != null) {
            Set<Integer> userFriendsIds = user.getFriends();

            HashMap<Integer, User> friends = new HashMap<>();

            for (Integer userId : userFriendsIds) {
                User u = userStorage.getUser(userId);
                friends.put(userId, u);
            }

            return friends.values();
        }

        return new HashSet<>();
    }

    public Collection<User> getCommonFriends(int id, int otherId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);

        if (user != null && otherUser != null) {
            Set<Integer> user1Friends = user.getFriends();
            Set<Integer> user2Friends = otherUser.getFriends();

            Set<Integer> intersection = new HashSet<>(user1Friends);

            intersection.retainAll(user2Friends);

            HashMap<Integer, User> commonUsers = new HashMap<>();

            for (Integer userId : intersection) {
                User u = userStorage.getUser(userId);
                commonUsers.put(userId, u);
            }

            return commonUsers.values();
        }

        return new HashSet<>();
    }
}
