package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto create(NewCompilationDto dto) {
        List<Event> events = dto.getEvents() == null
                ? List.of()
                : eventRepository.findAllById(dto.getEvents());

        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned() != null && dto.getPinned())
                .events(events)
                .build();

        Compilation saved = compilationRepository.save(compilation);

        CompilationDto result = mapper.toDto(saved);
        result.setEvents(
                saved.getEvents() == null
                        ? List.of()
                        : saved.getEvents()
                        .stream()
                        .map(eventMapper::toShortDto)
                        .collect(Collectors.toList())
        );
        return result;
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            compilation.setEvents(events);
        }

        Compilation saved = compilationRepository.save(compilation);

        CompilationDto result = mapper.toDto(saved);
        result.setEvents(
                saved.getEvents() == null
                        ? List.of()
                        : saved.getEvents()
                        .stream()
                        .map(eventMapper::toShortDto)
                        .collect(Collectors.toList())
        );
        return result;
    }

    @Override
    public void delete(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);

        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, page)
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }

        return compilationRepository.findAll(page)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        return mapper.toDto(compilation);
    }
}