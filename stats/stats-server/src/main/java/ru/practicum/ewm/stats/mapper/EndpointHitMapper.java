package ru.practicum.ewm.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "id", ignore = true)
    EndpointHit toEntity(HitDto dto);
}