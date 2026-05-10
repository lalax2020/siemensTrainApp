package org.example.routestop.dto;

import java.time.LocalTime;

public record RouteStopCreateDto(Long routeId, Long stationId, int stopOrder, LocalTime expectedTime) {}
