package ru.practicum.event;

import lombok.experimental.UtilityClass;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.event.dto.EventDtoResponse;
import ru.practicum.event.dto.EventDtoResponseShort;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.UserMapper;
import ru.practicum.user.dto.UserDtoResponseShort;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class EventMapper {
    public static EventDtoResponse toEventDtoResponse(Integer views, Integer requests, Event event) {
        return EventDtoResponse.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDtoResponse(event.getCategory()))
                .confirmedRequests(requests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserDtoResponseShort(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .build();
    }


    public static EventDtoResponseShort toEventDtoResponseShort(Integer view,
                                                                Integer requests,
                                                                CategoryDtoResponse categoryDto,
                                                                UserDtoResponseShort userDto,
                                                                Event event) {
        return EventDtoResponseShort.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(requests)
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(userDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(view)
                .build();
    }
}
