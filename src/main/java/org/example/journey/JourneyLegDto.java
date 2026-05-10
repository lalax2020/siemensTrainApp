package org.example.journey;

import java.time.LocalTime;

public record JourneyLegDto(
        String trainName,
        String fromStation,
        String toStation,
        LocalTime departure,
        LocalTime arrival,
        Long scheduleId
) {}