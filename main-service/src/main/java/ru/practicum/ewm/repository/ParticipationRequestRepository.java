package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEventId(Long eventId);
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);
    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);
}