package ru.yandex.practicum.filmorate.model;
import lombok.Data;

@Data
public class Event {
    private Integer id;
    private Integer userId;
    private Integer entityId;
    private EventType type;
    private Operation operation;
    private Long timestamp;
}
