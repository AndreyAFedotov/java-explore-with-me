package ru.practicum.enums;

public enum CommentSort {
    CREATED_ON("createdOn");

    private final String title;

    CommentSort(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
