package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.StatsDtoRequest;
import ru.practicum.model.App;
import ru.practicum.model.Hit;

@UtilityClass
public class StatsMapper {
    public static Hit toHit(StatsDtoRequest request, App app) {
        return Hit.builder()
                .app(app)
                .uri(request.getUri())
                .ip(request.getIp())
                .timestamp(request.getTimestamp())
                .build();
    }
}
