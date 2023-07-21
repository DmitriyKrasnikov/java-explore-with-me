package ru.practicum.ewm.hit.model;

import org.mapstruct.Mapper;
import ru.practicum.ewm.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface HitMapper {
    Hit toHit(EndpointHit endpointHit);
}
