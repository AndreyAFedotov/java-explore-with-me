package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDtoRequest;
import ru.practicum.compilation.dto.CompilationDtoResponseAll;
import ru.practicum.compilation.dto.CompilationDtoUpdateRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@AllArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDtoResponseAll createCompilationByAdmin(@Valid @RequestBody CompilationDtoRequest request) {
        log.info("admin:compilations - create new compilation with title: {}", request.getTitle());
        return compilationService.createCompilationByAdmin(request);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationByAdmin(@PathVariable Long compId) {
        log.info("admin:compilations - delete compilation with ID: {}", compId);
        compilationService.deleteCompilationByAdmin(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDtoResponseAll updateCompilationByAdmin(@PathVariable Long compId,
                                                              @Valid @RequestBody CompilationDtoUpdateRequest request) {
        log.info("admin:compilations - update compilation with ID: {}", compId);
        return compilationService.updateCompilationByAdmin(compId, request);
    }

}
