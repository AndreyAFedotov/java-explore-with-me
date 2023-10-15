package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.StatsDtoResponse;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitStorage extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.StatsDtoResponse(a.name, h.uri, count(h.ip)) " +
            "from Hit as h join fetch App as a on a.id = h.app.id " +
            "where h.timestamp >= :start and h.timestamp <= :end and ((coalesce(:uris, '') = '') or (h.uri in :uris)) " +
            "group by a.name, h.uri " +
            "order by count(h.ip) desc")
    List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.StatsDtoResponse(a.name, h.uri, count(distinct h.ip)) " +
            "from Hit as h join fetch App as a on a.id = h.app.id " +
            "where h.timestamp >= :start and h.timestamp <= :end and ((coalesce(:uris, '') = '') or (h.uri in :uris)) " +
            "group by a.name, h.uri " +
            "order by count(distinct h.ip) desc")
    List<StatsDtoResponse> getStatsUniq(LocalDateTime start, LocalDateTime end, List<String> uris);
}
;