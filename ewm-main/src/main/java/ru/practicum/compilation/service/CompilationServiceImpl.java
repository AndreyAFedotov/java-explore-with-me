package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.CompilationStorage;
import ru.practicum.compilation.dto.CompilationDtoRequest;
import ru.practicum.compilation.dto.CompilationDtoResponseAll;
import ru.practicum.compilation.dto.CompilationDtoUpdateRequest;
import ru.practicum.event.Event;
import ru.practicum.event.EventStorage;
import ru.practicum.event.dto.EventDtoResponseShort;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationStorage compilationStorage;
    private final EventStorage eventStorage;
    private final EventService eventService;

    @Override
    public CompilationDtoResponseAll createCompilationByAdmin(CompilationDtoRequest request) {
        if (request.getPinned() == null) {
            request.setPinned(false);
        }
        List<Event> events = eventStorage.findAllById(request.getEvents());
        Compilation compilation = compilationStorage.save(CompilationMapper.toCompilation(request, events));
        log.info("Create new compilation with ID: {}", compilation.getId());

        List<EventDtoResponseShort> ev = eventService.buildEventDtoResponseShort(events);
        return CompilationMapper.toCompilationDtoResponseAll(compilation, ev);
    }

    @Override
    public void deleteCompilationByAdmin(Long id) {
        if (compilationStorage.existsById(id)) {
            compilationStorage.deleteById(id);
            log.info("Compilation with ID {} was deleted", id);
        } else {
            throw new NotFoundException("Compilation with id=" + id + " was not found");
        }
    }

    @Override
    public CompilationDtoResponseAll updateCompilationByAdmin(Long id, CompilationDtoUpdateRequest request) {
        Compilation compilation = compilationStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));

        Optional.ofNullable(request.getPinned()).ifPresent(compilation::setPinned);
        Optional.ofNullable(request.getTitle()).ifPresent(compilation::setTitle);
        if (request.getEvents() != null) {
            compilation.setEvents(eventStorage.findAllById(request.getEvents()));
        }
        Compilation result = compilationStorage.save(compilation);
        log.info("Compilation with ID {} was updated", result.getId());

        List<EventDtoResponseShort> ev = eventService.buildEventDtoResponseShort(compilation.getEvents());
        return CompilationMapper.toCompilationDtoResponseAll(compilation, ev);
    }

    @Override
    public List<CompilationDtoResponseAll> getCompilationsPublic(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationStorage.findAllByPinned(pinned, pageable).toList();

        List<CompilationDtoResponseAll> result = compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationDtoResponseAll(
                        compilation, eventService.buildEventDtoResponseShort(compilation.getEvents())))
                .collect(Collectors.toList());
        log.info("Found {} compilations", result.size());
        return result;
    }

    @Override
    public CompilationDtoResponseAll getCompilationPublic(Long compId) {
        Compilation compilation = compilationStorage.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        log.info("Compilation with ID {} was found", compId);
        List<EventDtoResponseShort> ev = eventService.buildEventDtoResponseShort(compilation.getEvents());
        return CompilationMapper.toCompilationDtoResponseAll(compilation, ev);
    }
}
