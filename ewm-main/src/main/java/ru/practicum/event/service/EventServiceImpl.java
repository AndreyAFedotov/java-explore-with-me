package ru.practicum.event.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryStorage;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.enums.AdminStateAction;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventStorage;
import ru.practicum.event.QEvent;
import ru.practicum.event.dto.EventDtoRequest;
import ru.practicum.event.dto.EventDtoResponse;
import ru.practicum.event.dto.EventDtoResponseShort;
import ru.practicum.event.dto.EventDtoUpdateRequest;
import ru.practicum.exception.exceptions.AccessDeniedException;
import ru.practicum.exception.exceptions.NotFoundException;
import ru.practicum.exception.exceptions.ValidationException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationStorage;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.request.dto.RequestDtoResponse;
import ru.practicum.request.dto.RequestStatusUpdateRequest;
import ru.practicum.request.dto.RequestStatusUpdateResponse;
import ru.practicum.request.model.QRequest;
import ru.practicum.request.service.RequestService;
import ru.practicum.stat.RemoteStatService;
import ru.practicum.user.UserMapper;
import ru.practicum.user.dto.UserDtoResponseShort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventStorage eventStorage;
    private final RemoteStatService statService;
    private final RequestService requestService;
    private final CategoryStorage categoryStorage;
    private final LocationStorage locationStorage;

    @Override
    public List<EventDtoResponse> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                   String rangeStart, String rangeEnd, int from, int size) {

        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, FORMAT);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, FORMAT);
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start must be before end");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> events = eventStorage.getEventsByAdmin(users, states, categories, start, end, pageable);
        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Integer> views = statService.getStats(events.toList());
        Map<Long, Integer> requests = requestService.getConfirmedRequests(events.toList());
        List<EventDtoResponse> result = new ArrayList<>();
        for (Event event : events) {
            EventDtoResponse response = EventMapper.toEventDtoResponse(
                    views.getOrDefault(event.getId(), 0),
                    requests.getOrDefault(event.getId(), 0),
                    event);
            result.add(response);
        }

        log.info("{} events was found", result.size());
        return result;
    }

    @Override
    public EventDtoResponse updateEventByAdmin(Long id, EventDtoRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Event event = eventStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Event " + id + " not found"));

        if (request.getEventDate() != null && request.getEventDate().isBefore(now.plusHours(1))) {
            throw new AccessDeniedException("the time of event must be no earlier than an hour from the publication");
        }

        if (request.getStateAction() != null) {
            if(request.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new AccessDeniedException("Can publish only PENDING events");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(now);
            } else {
                if(event.getState().equals(EventState.PUBLISHED)) {
                    throw new AccessDeniedException("Can only reject an unpublished event");
                }
                event.setState(EventState.CANCELED);
            }
        }

        Optional.ofNullable(request.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(request.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(request.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(request.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(request.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(request.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(request.getTitle()).ifPresent(event::setTitle);
        if (request.getCategory() != null) {
            event.setCategory(getCategory(request.getCategory()));
        }
        if (request.getLocation() != null) {
            event.setLocation(getLocation(request.getLocation()));
        }
        Map<Long, Integer> views = statService.getStats(List.of(event));
        Map<Long, Integer> requests = requestService.getConfirmedRequests(List.of(event));
        Event resultEvent = eventStorage.save(event);
        log.info("Event with ID {} was updated.", resultEvent.getId());

        return EventMapper.toEventDtoResponse(
                views.getOrDefault(event.getId(), 0),
                requests.getOrDefault(event.getId(), 0),
                event);
    }



    @Override
    public List<EventDtoResponse> getUserEventsByUser(Long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventDtoResponse createEventByUser(Long userId, EventDtoRequest request) {
        return null;
    }

    @Override
    public EventDtoResponse getEventByUser(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventDtoResponse updateEventByUser(Long userId, Long eventId, EventDtoUpdateRequest request) {
        return null;
    }

    @Override
    public List<RequestDtoResponse> getEventRequestsByUser(Long userId, Long eventId) {
        return null;
    }

    @Override
    public List<RequestStatusUpdateResponse> updateEventRequestsByUser(Long userId, Long eventId,
                                                                       RequestStatusUpdateRequest request) {
        return null;
    }

    @Override
    public List<EventDtoResponseShort> getEventsPublic(String text,
                                                       List<Long> categories,
                                                       Boolean paid,
                                                       String rangeStart,
                                                       String rangeEnd,
                                                       Boolean available,
                                                       String sort,
                                                       int from,
                                                       int size,
                                                       HttpServletRequest request) {
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, FORMAT);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, FORMAT);
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start must be before end");
        }

        //TODO как решить с сортировкой и не исчерпавшими лимит?


        Pageable pageable = PageRequest.of(from / size, size);
        if (StringUtils.isNotBlank(sort) && sort.equals("EVENT_DATE")) {
            sort = "eventDate";
            pageable = PageRequest.of(from / size, size, Sort.by(sort).descending());
        }











        statService.createHit(request);
        return null;
    }

    @Override
    public EventDtoResponse getEventPublic(Long id, HttpServletRequest request) {
        return null;
    }

    @Override
    public List<EventDtoResponseShort> buildEventDtoResponseShort(List<Event> events) {
        List<EventDtoResponseShort> result = new ArrayList<>();
        Map<Long, Integer> views = statService.getStats(events);
        Map<Long, Integer> requests = requestService.getConfirmedRequests(events);
        for (Event event : events) {
            CategoryDtoResponse categoryDto = CategoryMapper.toCategoryDtoResponse(event.getCategory());
            UserDtoResponseShort userDto = UserMapper.toUserDtoResponseShort(event.getInitiator());
            EventDtoResponseShort response = EventMapper.toEventDtoResponseShort(
                    views.getOrDefault(event.getId(), 0),
                    requests.getOrDefault(event.getId(), 0),
                    categoryDto,
                    userDto,
                    event
            );
            result.add(response);
        }
        return result;
    }

    private Category getCategory(Long catId) {
        return categoryStorage.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category id=" + catId + " not found"));
    }

    private Location getLocation(LocationDto location) {
       Location result = locationStorage.findLocationByLatAndLon(location.getLat(), location.getLon());
       if (result == null) {
           result = locationStorage.save(LocationMapper.toLocation(location));
       }
       return result;
    }

}
