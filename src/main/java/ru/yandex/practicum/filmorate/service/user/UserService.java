package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final FeedStorage feedStorage;
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;

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

                    Event event = new Event();
                    event.setUserId(id);
                    event.setEntityId(friendId);
                    event.setType(EventType.FRIEND);
                    event.setOperation(Operation.ADD);
                    event.setTimestamp(Instant.now().toEpochMilli());

                    feedStorage.addEvent(event);
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

                Event event = new Event();
                event.setUserId(id);
                event.setEntityId(friendId);
                event.setType(EventType.FRIEND);
                event.setOperation(Operation.REMOVE);
                event.setTimestamp(Instant.now().toEpochMilli());

                feedStorage.addEvent(event);
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

    public Collection<EventDto> getFeed(int id) {
        return feedStorage.getUserEvents(id).stream().map(EventMapper::mapToEventDto).toList();
    }

    public Collection<FilmDto> getRecommendations(int userId) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));

        Optional<Integer> similarUserIdOpt = likeStorage.findMostSimilarUser(userId);

        if (similarUserIdOpt.isEmpty()) {
            return Collections.emptyList();
        }

        int similarUserId = similarUserIdOpt.get();

        Collection<Film> recommendedFilms = likeStorage.getRecommendedFilms(userId, similarUserId);

        return recommendedFilms.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
