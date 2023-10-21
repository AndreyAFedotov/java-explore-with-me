package ru.practicum.location;

import lombok.experimental.UtilityClass;
import ru.practicum.location.dto.LocationDto;

@UtilityClass
public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }

    public static Location toLocation(LocationDto location) {
        return Location.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }
}
