package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.RelationStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(int userId) {
        return userStorage.getUser(userId);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void removeUser(int id) {
        userStorage.removeUser(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(int id, int friendId) {
        if (id == friendId) {
            return;
        }

        User user = userStorage.getUser(id);

        if (user != null) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                Set<Friendship> userFriends = user.getFriends();
                userFriends.add(new Friendship(id, friendId, RelationStatus.CONFIRMED));
                // Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
                Set<Friendship> friendFriends = friend.getFriends();
                friendFriends.add(new Friendship(friendId, id, RelationStatus.CONFIRMED));
            }
        }
    }

    public void removeFriend(int id, int friendId) {
        User user = userStorage.getUser(id);

        if (user != null) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                Set<Friendship> userFriends = user.getFriends();
                Friendship userFriendshipToRemove =  userFriends.stream().filter(f -> f.getFollowedUserId() == friendId).findFirst().orElse(null);
                userFriends.remove(userFriendshipToRemove);
                Set<Friendship> friendFriends = friend.getFriends();
                Friendship friendFriendshipToRemove =  friendFriends.stream().filter(f -> f.getFollowedUserId() == id).findFirst().orElse(null);

                friendFriends.remove(friendFriendshipToRemove);
            }
        }
    }

    public Collection<User> getFriends(int id) {
        User user = userStorage.getUser(id);

        if (user != null) {
            Set<Friendship> userFriends = user.getFriends();

            ArrayList<User> friends = new ArrayList<>();

            for (Friendship f: userFriends) {
                User u = userStorage.getUser(f.getFollowedUserId());
                friends.add(u);
            }

            return friends;
        }

        return new ArrayList<>();
    }

    public Collection<User> getCommonFriends(int id, int otherId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);

        if (user != null && otherUser != null) {
            Set<Integer> user1Friends = user.getFriends().stream().map(Friendship::getFollowedUserId).collect(Collectors.toSet());
            Set<Integer> user2Friends = otherUser.getFriends().stream().map(Friendship::getFollowedUserId).collect(Collectors.toSet());

            Set<Integer> intersection = new HashSet<>(user1Friends);

            intersection.retainAll(user2Friends);

            ArrayList<User> commonUsers = new ArrayList<>();

            for (Integer userId : intersection) {
                User u = userStorage.getUser(userId);
                commonUsers.add(u);
            }

            return commonUsers;
        }

        return new ArrayList<>();
    }
}
