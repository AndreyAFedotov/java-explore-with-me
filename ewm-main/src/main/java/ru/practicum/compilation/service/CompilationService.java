package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDtoRequest;
import ru.practicum.compilation.dto.CompilationDtoResponse;
import ru.practicum.compilation.dto.CompilationDtoResponseAll;

import java.util.List;

public interface CompilationService {

    CompilationDtoResponseAll createCompilationByAdmin(CompilationDtoRequest request);

    void deleteCompilationByAdmin(Long id);

    CompilationDtoResponseAll updateCompilationByAdmin(Long id, CompilationDtoRequest request);

    List<CompilationDtoResponseAll> getCompilationsPublic(Boolean pinned, int from, int size);

    CompilationDtoResponseAll getCompilationPublic(Long compId);
}
