package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDtoResponseAll;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDtoResponseAll> getCompilationsPublic(@RequestParam(defaultValue = "false") Boolean pinned,
                                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("public:compilations - get compilations");
        return compilationService.getCompilationsPublic(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDtoResponseAll getCompilationPublic(@PathVariable Long compId) {
        log.info("public:compilations - get compilation ID: {}", compId);
        return compilationService.getCompilationPublic(compId);
    }
}
