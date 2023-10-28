package ru.practicum.enums;

public enum EventSort {
    EVENT_DATE("eventDate"),
    ID("id"),
    VIEWS("views");

    private final String title;

    EventSort(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
