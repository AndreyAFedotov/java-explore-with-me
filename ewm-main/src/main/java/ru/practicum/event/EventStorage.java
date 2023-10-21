package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public interface EventStorage extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Boolean existsByCategoryId(Long id);

    @Query("select ev from Event as ev " +
            "where (:users is null or ev.initiator.id in :users) " +
            "and (:states is null or ev.state in :states) " +
            "and (:categories is null or ev.category.id in :categories) " +
            "and (coalesce(:start, null) is null or ev.eventDate > :start) " +
            "and (coalesce(:end, null) is null or ev.eventDate < :end)")
    Page<Event> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                 LocalDateTime start, LocalDateTime end, Pageable pageable);


}
