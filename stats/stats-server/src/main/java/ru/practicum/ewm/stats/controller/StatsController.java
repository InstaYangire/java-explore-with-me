package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.StatsDto;
import ru.practicum.ewm.stats.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
public class StatsController {

    private final StatsService service;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody @Validated HitDto hitDto) {
        service.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime end,

            @RequestParam(required = false)
            List<String> uris,

            @RequestParam(defaultValue = "false")
            boolean unique
    ) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        return service.getStats(start, end, uris, unique);
    }
}