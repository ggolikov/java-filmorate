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

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        if (user != null) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                Set<Integer> userFriends = user.getFriends();
                userFriends.add(friendId);
                // Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
                Set<Integer> friendFriends = friend.getFriends();
                friendFriends.add(userId);
            }
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        if (user != null) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                Set<Integer> userFriends = user.getFriends();
                userFriends.remove(friendId);
            }
        }
    }

    public Collection<User> getCommonFriends(int user1Id, int user2Id) {
        User user1 = userStorage.getUser(user1Id);
        User user2 = userStorage.getUser(user2Id);
        if (user1 != null && user2 != null) {
            Set<Integer> user1Friends = user1.getFriends();
            Set<Integer> user2Friends = user2.getFriends();

            Set<Integer> intersection = new HashSet<>(user1Friends);

            intersection.retainAll(user2Friends);

            HashMap<Integer, User> commonUsers = new HashMap<>();

            for (Integer userId : intersection) {
                User user = userStorage.getUser(userId);
                commonUsers.put(userId, user);
            }

            return commonUsers.values();
        }

        return new HashSet<>();
    }
}
