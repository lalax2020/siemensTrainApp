package org.example.schedule.dto;

import java.time.DayOfWeek;
import java.util.Set;

public record ScheduleDto(Long id, Long trainId, Set<DayOfWeek> daysOfWeek){
}
