package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "status", expression = "java(request.getStatus().name())")
    @Mapping(target = "created", expression = "java(mapDate(request.getCreated()))")
    ParticipationRequestDto toDto(ParticipationRequest request);

    default String mapDate(LocalDateTime date) {
        if (date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }
}