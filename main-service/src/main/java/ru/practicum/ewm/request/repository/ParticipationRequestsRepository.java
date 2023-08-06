package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.RequestState;

import java.util.List;

public interface ParticipationRequestsRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByEventId(Long eventId);

    Integer countByEventIdAndStatus(Long eventId, RequestState state);

    List<ParticipationRequest> findByIdInAndEventIdAndStatusOrderByCreatedAsc(List<Long> requestIds, Long eventId, RequestState pending);

    List<ParticipationRequest> findByRequesterId(Long userId);
}
