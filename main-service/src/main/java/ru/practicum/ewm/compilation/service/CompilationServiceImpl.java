package ru.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.CompilationMapper;
import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.FromSizePage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Get compilations pinned = {}, from = {}, size = {}", pinned, from, size);

        Pageable page = FromSizePage.ofFromSize(from, size);

        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findByPinned(pinned, page);
        }

        return compilations.stream().map(compilationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        log.info("Get compilation id = {}", compId);

        return compilationMapper.toDto(compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%s was not found", compId))));
    }

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("Create compilation title = {}", newCompilationDto.getTitle());

        if (newCompilationDto.getEvents() != null) {
            List<Long> events = newCompilationDto.getEvents();
            Set<Long> uniqueEvents = new HashSet<>(events);

            if (uniqueEvents.size() != events.size()) {
                throw new ConflictException("The request was made incorrectly");
            }
        }

        return compilationMapper.toDto(compilationRepository.save(compilationMapper.toCompilation(newCompilationDto)));
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        log.info("Delete compilation id = {}", compId);

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id = %s not found", compId)));

        compilationRepository.delete(compilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        log.info("Update compilation id = {}", compId);

        if (request.getEvents() != null) {
            List<Long> events = request.getEvents();
            Set<Long> uniqueEvents = new HashSet<>(events);

            if (uniqueEvents.size() != events.size()) {
                throw new ConflictException("The request was made incorrectly");
            }
        }

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id = %s not found", compId)));

        compilationMapper.updateCompilationFromDto(request, compilation);

        return compilationMapper.toDto(compilation);
    }

}
