package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Friendship {
    private int followingUserId;
    private int followedUserId;
    private RelationStatus relationStatus;

    public Friendship(int followingUserId, int followedUserId, RelationStatus relationStatus) {
        this.followingUserId = followingUserId;
        this.followedUserId = followedUserId;
        this.relationStatus = relationStatus;
    }
}
