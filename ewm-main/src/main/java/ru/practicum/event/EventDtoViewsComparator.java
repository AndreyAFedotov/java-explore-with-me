package ru.practicum.event;

import ru.practicum.event.dto.EventDtoResponseShort;

import java.util.Comparator;

public class EventDtoViewsComparator implements Comparator<EventDtoResponseShort> {
    @Override
    public int compare(EventDtoResponseShort o1, EventDtoResponseShort o2) {
        return o1.getViews() - o2.getViews();
    }
}
