package ru.practicum.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
import ru.practicum.enums.*;
import ru.practicum.event.*;
import ru.practicum.event.dto.*;
import ru.practicum.exception.exceptions.AccessDeniedException;
import ru.practicum.exception.exceptions.DateException;
import ru.practicum.exception.exceptions.NotFoundException;
import ru.practicum.exception.exceptions.ValidationException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationStorage;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestStorage;
import ru.practicum.request.dto.RequestDtoResponse;
import ru.practicum.request.dto.RequestStatusUpdateRequest;
import ru.practicum.request.dto.RequestStatusUpdateResponse;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestService;
import ru.practicum.stat.RemoteStatService;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserStorage;
import ru.practicum.user.dto.UserDtoResponseShort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;

    @Override
    public List<EventDtoResponse> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                   String rangeStart, String rangeEnd, int from, int size) {

        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, FORMAT);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, FORMAT);
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start must be before end");
        }

        BooleanExpression filter = buildConditionsForEventsByAdmin(users, states, categories, start, end);
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> events = eventStorage.findAll(filter, pageable);

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
    public EventDtoResponse updateEventByAdmin(Long id, EventDtoUpdateRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Event event = eventStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Event id=" + id + " not found"));

        if (request.getEventDate() != null && request.getEventDate().isBefore(now.plusHours(1))) {
            throw new DateException("the time of event must be no earlier than an hour from the publication");
        }

        UpdateStateAction actionReq = request.getStateAction();
        if(actionReq != null && actionReq.equals(UpdateStateAction.PUBLISH_EVENT)) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new AccessDeniedException("Can publish only PENDING events");
            }
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(now);
        } else if (actionReq != null) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new AccessDeniedException("Can only reject an unpublished event");
            }
            event.setState(EventState.CANCELED);
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
    public List<EventDtoResponseShort> getUserEventsByUser(Long userId, Integer from, Integer size) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User id=" + userId + " not found");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Event> events = eventStorage.findAllByInitiatorId(userId, pageable);

        log.info("For user {} founded {} events", userId, events.getSize());
        return buildEventDtoResponseShort(events.toList());
    }

    @Override
    public EventDtoResponse createEventByUser(Long userId, EventDtoRequest request) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id=" + userId + " not found"));

        if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new AccessDeniedException("The time of the event cannot be earlier than two hours");
        }
        if (request.getPaid() == null) {
            request.setPaid(false);
        }
        if (request.getParticipantLimit() == null) {
            request.setParticipantLimit(0);
        }
        if (request.getRequestModeration() == null) {
            request.setRequestModeration(true);
        }

        Event result = eventStorage.save(EventMapper.toEvent(
                request,
                user,
                getCategory(request.getCategory()),
                getLocation(request.getLocation())
        ));

        log.info("Event with ID {} was created by User {}", result.getId(), result.getInitiator().getId());
        return EventMapper.toEventDtoResponse(0, 0, result);
    }

    @Override
    public EventDtoResponse getEventByUser(Long userId, Long eventId) {
        if (!userStorage.existsUserById(userId)) {
            throw new NotFoundException("User id=" + userId + " not found");
        }
        Event event = eventStorage.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event id=" + eventId + " not found"));

        Map<Long, Integer> views = statService.getStats(List.of(event));
        Map<Long, Integer> requests = requestService.getConfirmedRequests(List.of(event));

        log.info("Event with ID {} founded by user id {}", event.getId(), event.getInitiator().getId());
        return EventMapper.toEventDtoResponse(
                views.getOrDefault(event.getId(), 0),
                requests.getOrDefault(event.getId(), 0),
                event);
    }

    @Override
    public EventDtoResponse updateEventByUser(Long userId, Long eventId, EventDtoUserRequest request) {
        Event event = eventStorage.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event id=" + eventId + " not found"));

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User id=" + userId + " not found");
        }
        if (!userId.equals(event.getInitiator().getId())) {
            throw new AccessDeniedException("User id=" + userId + "not owner of event id=" + event.getId());
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new AccessDeniedException("Event id=" + event.getId() + " in PUBLISHED state");
        }
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DateException("The time of the event cannot be earlier than two hours");
        } else {
            Optional.ofNullable(request.getEventDate()).ifPresent(event::setEventDate);
        }
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(UpdateEventStatus.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
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

        Event result = eventStorage.save(event);

        Map<Long, Integer> views = statService.getStats(List.of(event));
        Map<Long, Integer> requests = requestService.getConfirmedRequests(List.of(event));

        log.info("Event ID {} was update", result.getId());

        return EventMapper.toEventDtoResponse(
                views.getOrDefault(event.getId(), 0),
                requests.getOrDefault(event.getId(), 0),
                event);
    }

    @Override
    public List<RequestDtoResponse> getEventRequestsByUser(Long userId, Long eventId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User id=" + userId + " not found");
        }
        Event event = eventStorage.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event id=" + eventId + " not found"));
        if (!userId.equals(event.getInitiator().getId())) {
            throw new AccessDeniedException("User id=" + userId + "not owner of event id=" + event.getId());
        }

        List<Request> requests = requestStorage.findAllByEventId(eventId);
        log.info("{} requests found for event ID {} by user ID {}", requests.size(), eventId, userId);

        return requests.stream()
                .map(RequestMapper::toRequestDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RequestStatusUpdateResponse updateEventRequestsByUser(Long userId, Long eventId,
                                                                 RequestStatusUpdateRequest request) {
        Event event = eventStorage.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event id=" + eventId + " not found"));
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User id=" + userId + " not found");
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new AccessDeniedException("Confirmation of the request is not required");
        }
        Map<Long, Integer> requestsMap = requestService.getConfirmedRequests(List.of(event));
        int requestsCount = requestsMap.getOrDefault(eventId, 0);
        if (requestsCount >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new AccessDeniedException("Limit reached");
        }

        RequestStatusUpdateResponse result = new RequestStatusUpdateResponse();
        for (Long reqId : request.getRequestIds()) {
            Request nextRequest = requestStorage.findById(reqId)
                    .orElseThrow(() -> new NotFoundException("Request id=" + reqId + " not found"));

            if (!nextRequest.getStatus().equals(RequestStatus.PENDING)) {
                throw new AccessDeniedException("Request id=" + reqId + " not in PENDING status");
            }

            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                if (requestsCount == event.getParticipantLimit()) {
                    nextRequest.setStatus(RequestStatus.REJECTED);
                    result.getRejectedRequests().add(RequestMapper.toRequestDtoResponse(nextRequest));
                } else {
                    nextRequest.setStatus(RequestStatus.CONFIRMED);
                    result.getConfirmedRequests().add(RequestMapper.toRequestDtoResponse(nextRequest));
                    requestsCount++;
                }
            } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
                nextRequest.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(RequestMapper.toRequestDtoResponse(nextRequest));
            }
            requestStorage.save(nextRequest);
        }
        return result;
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

        BooleanExpression filter = buildConditionsForEventsPublic(text, categories, paid, start, end, available);
        Sort doSort;
        if (StringUtils.isNotBlank(sort) && sort.equals(EventSort.EVENT_DATE.toString())) {
            doSort = Sort.by(EventSort.EVENT_DATE.getTitle());
        } else {
            doSort = Sort.by(EventSort.ID.getTitle());
        }
        Pageable pageable = PageRequest.of(from / size, size, doSort);
        Page<Event> events = eventStorage.findAll(filter, pageable);
        List<EventDtoResponseShort> result = buildEventDtoResponseShort(events.toList());
        if (StringUtils.isNotBlank(sort) && sort.equals(EventSort.VIEWS.toString())) {
            Comparator<EventDtoResponseShort> comparator = new EventDtoViewsComparator();
            result.sort(comparator);
        }

        return result;
    }

    @Override
    public EventDtoResponse getEventPublic(Long id, HttpServletRequest request) {
        Event event = eventStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("The event id=" + id + " is unpublished");
        }

        Map<Long, Integer> views = statService.getStats(List.of(event));
        Map<Long, Integer> requests = requestService.getConfirmedRequests(List.of(event));

        return EventMapper.toEventDtoResponse(
                views.getOrDefault(event.getId(), 0),
                requests.getOrDefault(event.getId(), 0),
                event);
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

    private BooleanExpression buildConditionsForEventsByAdmin(List<Long> users,
                                                              List<EventState> states,
                                                              List<Long> categories,
                                                              LocalDateTime start,
                                                              LocalDateTime end) {

        List<BooleanExpression> conditions = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            BooleanExpression condition = QEvent.event.initiator.id.in(users);
            condition.and(condition);
        }
        if (states != null && !states.isEmpty()) {
            BooleanExpression condition = QEvent.event.state.in(states);
            conditions.add(condition);
        }
        if (categories != null && !categories.isEmpty()) {
            BooleanExpression condition = QEvent.event.category.id.in(categories);
            conditions.add(condition);
        }
        if (start != null) {
            BooleanExpression condition = QEvent.event.eventDate.after(start);
            conditions.add(condition);
        }
        if (end != null) {
            BooleanExpression condition = QEvent.event.eventDate.before(end);
            conditions.add(condition);
        }
        if (conditions.isEmpty()) {
            return Expressions.TRUE.isTrue();
        } else {
            return conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
        }
    }

    private BooleanExpression buildConditionsForEventsPublic(String text,
                                                             List<Long> categories,
                                                             Boolean paid,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             Boolean available) {

        List<BooleanExpression> conditions = new ArrayList<>();
        if (StringUtils.isNotBlank(text)) {
            BooleanExpression condition = QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text));
            conditions.add(condition);
        }
        if (categories != null && !categories.isEmpty()) {
            BooleanExpression condition = QEvent.event.category.id.in(categories);
            conditions.add(condition);
        }
        if (paid != null) {
            BooleanExpression condition = QEvent.event.paid.eq(paid);
            conditions.add(condition);
        }
        if (start != null) {
            BooleanExpression condition = QEvent.event.eventDate.after(start);
            conditions.add(condition);
        }
        if (end != null) {
            BooleanExpression condition = QEvent.event.eventDate.before(end);
            conditions.add(condition);
        }
        if (available != null) {
            BooleanExpression condition = QEvent.event.participantLimit.goe(0);
            conditions.add(condition);
        }
        BooleanExpression condition = QEvent.event.state.eq(EventState.PUBLISHED);
        conditions.add(condition);

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
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
