package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface FeedStorage {
    Event addEvent(Event event);

    Collection<Event> getUserEvents(int userId);
}
