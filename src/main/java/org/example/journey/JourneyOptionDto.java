package org.example.journey;

import java.time.LocalTime;
import java.util.List;

public record JourneyOptionDto(
        List<JourneyLegDto> legs,
        LocalTime totalDeparture,
        LocalTime totalArrival
) {}