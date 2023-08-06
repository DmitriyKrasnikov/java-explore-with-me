package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.model.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.exception.ConflictException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/compilations")
@AllArgsConstructor
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Validated NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getEvents() != null) {
            List<Long> events = newCompilationDto.getEvents();
            Set<Long> uniqueEvents = new HashSet<>(events);

            if (uniqueEvents.size() != events.size()) {
                throw new ConflictException("The request was made incorrectly");
            }
        }

        return compilationService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable(name = "compId") Long compId,
                                            @RequestBody @Validated UpdateCompilationRequest request) {
        if (request.getEvents() != null) {
            List<Long> events = request.getEvents();
            Set<Long> uniqueEvents = new HashSet<>(events);

            if (uniqueEvents.size() != events.size()) {
                throw new ConflictException("The request was made incorrectly");
            }
        }

        return compilationService.updateCompilation(compId, request);
    }
}
