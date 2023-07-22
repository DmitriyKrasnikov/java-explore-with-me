package ru.practicum.ewm.hit.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.hit.model.Hit;
import ru.practicum.ewm.hit.model.HitMapper;
import ru.practicum.ewm.hit.repository.HitRepository;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HitServiceImpl implements HitService{

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Override
    @Transactional
    public void createHit(EndpointHit endpointHit) {
        log.info("Create hit {}", endpointHit);
        Hit hit = hitMapper.toHit(endpointHit);
        hitRepository.save(hit);
    }

    @Override
    @Transactional
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("GetStats uris {}, unique {}, start {}, end {}", uris, unique, start, end);
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
