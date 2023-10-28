package ru.practicum.compilation;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDtoRequest;
import ru.practicum.compilation.dto.CompilationDtoResponseAll;
import ru.practicum.event.Event;
import ru.practicum.event.dto.EventDtoResponseShort;

import java.util.List;

@UtilityClass
public class CompilationMapper {
    public static Compilation toCompilation(CompilationDtoRequest request, List<Event> events) {
        return Compilation.builder()
                .pinned(request.getPinned())
                .title(request.getTitle())
                .events(events)
                .build();
    }

    public static CompilationDtoResponseAll toCompilationDtoResponseAll(
            Compilation compilation,
            List<EventDtoResponseShort> eventDtoResponseShorts) {
        return CompilationDtoResponseAll.builder()
                .events(eventDtoResponseShorts)
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
