package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Optional;

public interface FriendshipStorage {

    Optional<Friendship> getFriendship(int followingUserId, int followedUserId);

    void addFriendship(int followingUserId, int followedUserId, String status);

    void removeFriendship(int id, int friendId);
}
