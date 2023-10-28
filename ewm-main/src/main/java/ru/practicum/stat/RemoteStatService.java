package ru.practicum.stat;

import ru.practicum.event.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface RemoteStatService {

    void createHit(HttpServletRequest request);

    Map<Long, Integer> getStats(List<Event> events);

}
