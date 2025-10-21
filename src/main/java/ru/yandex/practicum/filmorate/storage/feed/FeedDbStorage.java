package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import java.util.Collection;

@Repository
@Qualifier("feedDbStorage")
public class FeedDbStorage extends BaseStorage<Event> implements FeedStorage {
    private static final String INSERT_EVENT_QUERY = "INSERT INTO events(timestamp, user_id, entity_id, type, operation)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String USER_EVENTS_QUERY =
            """
                SELECT *
                    FROM events
                    WHERE user_id = ?
            """;

    public FeedDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new EventRowMapper());
    }

    public Event addEvent(Event event) {
        int id = insert(
                INSERT_EVENT_QUERY,
                event.getTimestamp(),
                event.getUserId(),
                event.getEntityId(),
                event.getType().name(),
                event.getOperation().name()
        );
        event.setId(id);
        return event;
    }

    public Collection<Event> getUserEvents(int id) {
        return findMany(USER_EVENTS_QUERY, id);
    }
}
