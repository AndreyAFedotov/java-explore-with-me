package ru.practicum.location;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationStorage extends JpaRepository<Location, Long> {

    Location findLocationByLatAndLon(Float lat, Float lon);

}
