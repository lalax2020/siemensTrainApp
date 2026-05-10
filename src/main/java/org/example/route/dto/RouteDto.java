package org.example.route.dto;

import org.example.routestop.dto.RouteStopDto;

import java.util.List;

public record RouteDto(Long id, String name, List<RouteStopDto> stops) {}
