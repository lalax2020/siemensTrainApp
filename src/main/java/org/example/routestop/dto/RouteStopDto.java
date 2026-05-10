package org.example.routestop.dto;

import java.time.LocalTime;

public record RouteStopDto(Long id, String stationName,
                           int stopOrder, LocalTime expectedTime, LocalTime actualTime, Integer delayMinutes) {
}
