package ru.practicum.ewm.hit.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.hit.model.Hit;
import ru.practicum.ewm.hit.model.HitMapper;
import ru.practicum.ewm.hit.repository.HitRepository;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class HitService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    public void createHit(EndpointHit endpointHit) {
        Hit hit = hitMapper.toHit(endpointHit);
        hitRepository.save(hit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStats> stats;
        if (uris.isEmpty()) {
            if (!unique) {
                return hitRepository.getStatsWithoutUrisNotUnique(start, end);
            }
            return hitRepository.getStatsWithoutUrisUnique(start, end);
        }
        if (!unique) {
            return hitRepository.getStatsWithUrisNotUnique(uris, start, end);
        }
        return hitRepository.getStatsWithUrisUnique(uris, start, end);
    }
}
