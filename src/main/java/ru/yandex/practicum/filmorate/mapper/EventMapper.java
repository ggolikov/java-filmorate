package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {
    public static EventDto mapToEventDto(Event event) {
        EventDto dto = new EventDto();
        dto.setEventId(event.getId());
        dto.setEntityId(event.getEntityId());
        dto.setUserId(event.getUserId());
        dto.setTimestamp(event.getTimestamp());
        dto.setOperation(event.getOperation().name());
        dto.setEventType(event.getType().name());
        return dto;
    }
}