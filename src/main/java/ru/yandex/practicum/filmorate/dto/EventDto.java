package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class EventDto {
    private Integer eventId;
    private Integer userId;
    private Integer entityId;
    private String eventType;
    private String operation;
    private Long timestamp;
}
