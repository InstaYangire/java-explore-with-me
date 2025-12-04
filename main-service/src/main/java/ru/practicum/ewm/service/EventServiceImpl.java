package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private EventFullDto enrichFull(Event event) {
        EventFullDto dto = eventMapper.toFullDto(event);

        Long confirmed = requestRepository.countByEventIdAndStatus(
                event.getId(), RequestStatus.CONFIRMED
        );
        dto.setConfirmedRequests(confirmed.intValue());

        List<ViewStatsDto> stats = statsClient.getStats(
                "2000-01-01 00:00:00",
                "3000-01-01 00:00:00",
                List.of("/events/" + event.getId()),
                true
        );

        dto.setViews(stats.isEmpty() ? 0 : stats.get(0).getHits());

        return dto;
    }

    private EventShortDto enrichShort(Event event) {
        EventShortDto dto = eventMapper.toShortDto(event);

        Long confirmed = requestRepository.countByEventIdAndStatus(
                event.getId(), RequestStatus.CONFIRMED
        );
        dto.setConfirmedRequests(confirmed.intValue());

        List<ViewStatsDto> stats = statsClient.getStats(
                "2000-01-01 00:00:00",
                "3000-01-01 00:00:00",
                List.of("/events/" + event.getId()),
                true
        );

        dto.setViews(stats.isEmpty() ? 0 : stats.get(0).getHits().intValue());

        return dto;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), formatter);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event date must be at least 2 hours in the future");
        }

        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new BadRequestException("Participant limit cannot be negative");
        }

        Location location = locationRepository.save(
                Location.builder()
                        .lat(dto.getLocation().getLat())
                        .lon(dto.getLocation().getLon())
                        .build()
        );

        Event event = Event.builder()
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(eventDate)
                .createdOn(LocalDateTime.now())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() == null || dto.getRequestModeration())
                .state(EventState.PENDING)
                .initiator(initiator)
                .category(category)
                .location(location)
                .build();

        return enrichFull(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        return eventRepository.findAllByInitiatorId(userId)
                .stream()
                .skip(from)
                .limit(size)
                .map(this::enrichShort)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return enrichFull(event);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "SEND_TO_REVIEW":
                    if (event.getState() != EventState.CANCELED) {
                        throw new ConflictException("Only canceled events can be sent to review");
                    }
                    event.setState(EventState.PENDING);
                    break;

                case "CANCEL_REVIEW":
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Only pending events can be canceled");
                    }
                    event.setState(EventState.CANCELED);
                    break;

                default:
                    throw new BadRequestException("Unknown state action: " + dto.getStateAction());
            }
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newDate = LocalDateTime.parse(dto.getEventDate(), formatter);
            if (newDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Event date must be at least 2 hours in the future");
            }
            event.setEventDate(newDate);
        }

        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new BadRequestException("Participant limit cannot be negative");
        }

        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }

        if (dto.getLocation() != null) {
            Location location = locationRepository.save(
                    Location.builder()
                            .lat(dto.getLocation().getLat())
                            .lon(dto.getLocation().getLon())
                            .build()
            );
            event.setLocation(location);
        }

        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());

        return enrichFull(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> searchEventsAdmin(
            List<Long> users, List<String> states, List<Long> categories,
            String rangeStart, String rangeEnd, Integer from, Integer size) {

        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter);

        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException("Start must be before end");
        }

        return eventRepository.findAll(PageRequest.of(from / size, size)).stream()
                .filter(e -> users == null || users.isEmpty() || users.contains(e.getInitiator().getId()))
                .filter(e -> states == null || states.isEmpty() || states.contains(e.getState().name()))
                .filter(e -> categories == null || categories.isEmpty() || categories.contains(e.getCategory().getId()))
                .filter(e -> {
                    if (start == null) return true;
                    LocalDateTime date = e.getEventDate();
                    return !date.isBefore(start);
                })
                .filter(e -> {
                    if (end == null) return true;
                    LocalDateTime date = e.getEventDate();
                    return !date.isAfter(end);
                })
                .map(this::enrichFull)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (dto.getEventDate() != null) {
            LocalDateTime newDate = LocalDateTime.parse(dto.getEventDate(), formatter);
            if (newDate.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Event date must not be in the past");
            }
            event.setEventDate(newDate);
        }

        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new BadRequestException("Participant limit cannot be negative");
        }

        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }

        if (dto.getLocation() != null) {
            Location location = locationRepository.save(
                    Location.builder()
                            .lat(dto.getLocation().getLat())
                            .lon(dto.getLocation().getLon())
                            .build()
            );
            event.setLocation(location);
        }

        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "PUBLISH_EVENT":
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
                    }
                    if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                        throw new ConflictException("Event date must be at least 1 hour after publication");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;

                case "REJECT_EVENT":
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot reject the event because it is already published");
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        return enrichFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() != EventState.PENDING) {
            throw new ConflictException("Only pending events can be published");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Event date must be at least 1 hour after publication");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());

        return enrichFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() != EventState.PENDING) {
            throw new ConflictException("Only pending events can be rejected");
        }

        event.setState(EventState.CANCELED);

        return enrichFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto getPublicEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event not published");
        }

        return enrichFull(event);
    }

    @Override
    public List<EventShortDto> searchPublicEvents(
            String text, List<Long> categories, Boolean paid,
            String rangeStart, String rangeEnd,
            Boolean onlyAvailable, String sort,
            Integer from, Integer size) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = rangeStart == null ? now :
                LocalDateTime.parse(rangeStart, formatter);

        LocalDateTime end = rangeEnd == null ? LocalDateTime.MAX :
                LocalDateTime.parse(rangeEnd, formatter);

        if (start.isAfter(end)) {
            throw new BadRequestException("Start must be before end");
        }

        List<Event> events = eventRepository.findAll()
                .stream()
                .filter(e -> e.getState() == EventState.PUBLISHED)
                .filter(e -> !e.getEventDate().isBefore(start))
                .filter(e -> !e.getEventDate().isAfter(end))
                .filter(e -> text == null || e.getAnnotation().toLowerCase().contains(text.toLowerCase())
                        || e.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(e -> categories == null || categories.contains(e.getCategory().getId()))
                .filter(e -> paid == null || e.getPaid().equals(paid))
                .collect(Collectors.toList());

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(e -> {
                        Long confirmed = requestRepository.countByEventIdAndStatus(
                                e.getId(), RequestStatus.CONFIRMED);
                        return e.getParticipantLimit() == 0 || confirmed < e.getParticipantLimit();
                    })
                    .collect(Collectors.toList());
        }

        List<EventShortDto> dtos = events.stream()
                .map(this::enrichShort)
                .collect(Collectors.toList());

        if ("VIEWS".equalsIgnoreCase(sort)) {
            dtos.sort((a, b) -> b.getViews().compareTo(a.getViews()));
        } else {
            dtos.sort((a, b) -> {
                LocalDateTime d1 = LocalDateTime.parse(a.getEventDate(), formatter);
                LocalDateTime d2 = LocalDateTime.parse(b.getEventDate(), formatter);
                return d1.compareTo(d2);
            });
        }

        return dtos.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }
}
