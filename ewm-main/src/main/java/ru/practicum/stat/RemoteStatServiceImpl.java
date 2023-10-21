package ru.practicum.stat;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.event.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class RemoteStatServiceImpl implements RemoteStatService {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient client;

    @Override
    public void createHit(HttpServletRequest request) {
        log.info("New hit to remote stat added");
        client.createHit(request);
    }

    @Override
    public Map<Long, Integer> getStats(List<Event> events) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start;
        List<String> uris = new ArrayList<>();
        Map<Long, Integer> result = new HashMap<>();

        for (Event env : events) {
            if (env.getPublishedOn() != null && env.getPublishedOn().isBefore(start)) {
                start = env.getPublishedOn();
            }
            uris.add("/events/" + env.getId());
        }
        ResponseEntity<Object> response = client.getStats(start.format(FORMAT), end.format(FORMAT), uris, true);

        //TODO читаем статистику --- надо отлаживать, так сложно понять


        return null;

    }

}
