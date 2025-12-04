package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.*;

import java.util.List;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto dto);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    List<EventFullDto> searchEventsAdmin(
            List<Long> users, List<String> states, List<Long> categories,
            String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    EventFullDto getPublicEvent(Long eventId);

    List<EventShortDto> searchPublicEvents(
            String text, List<Long> categories, Boolean paid,
            String rangeStart, String rangeEnd,
            Boolean onlyAvailable, String sort,
            Integer from, Integer size);
}