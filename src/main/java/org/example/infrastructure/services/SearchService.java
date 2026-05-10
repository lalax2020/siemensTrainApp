package org.example.infrastructure.services;

import lombok.RequiredArgsConstructor;
import org.example.infrastructure.exceptions.NotFoundException;
import org.example.journey.JourneyLegDto;
import org.example.journey.JourneyOptionDto;
import org.example.routestop.entity.RouteStop;
import org.example.schedule.entity.Schedule;
import org.example.schedule.repo.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ScheduleRepository scheduleRepo;

    public List<JourneyOptionDto> findJourneys(String from, String to, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        List<Schedule> schedules = scheduleRepo.findScheduleByDays(day);

        Map<String, List<Schedule>> stationToSchedules = new HashMap<>();
        for (Schedule s : schedules) {
            for (RouteStop rs : s.getTrain().getRoute().getStops()) {
                stationToSchedules
                        .computeIfAbsent(rs.getStation().getName(), k -> new ArrayList<>())
                        .add(s);
            }
        }

        List<JourneyOptionDto> results = new ArrayList<>();

        for (Schedule s1 : schedules) {
            List<RouteStop> stops1 = s1.getTrain().getRoute().getStops();
            int fromIdx = indexOfStation(stops1, from);
            if (fromIdx == -1) continue;

            for (RouteStop rs1 : stops1.subList(fromIdx + 1, stops1.size())) {
                String transferStation = rs1.getStation().getName();

                // ruta directa
                if (transferStation.equalsIgnoreCase(to)) {
                    results.add(directJourney(s1, stops1, fromIdx, stops1.indexOf(rs1)));
                    continue;
                }

                // changeover
                List<Schedule> candidates = stationToSchedules
                        .getOrDefault(transferStation, List.of());

                for (Schedule s2 : candidates) {
                    if (s2.equals(s1)) continue;
                    List<RouteStop> stops2 = s2.getTrain().getRoute().getStops();
                    int transferIdx2 = indexOfStation(stops2, transferStation);
                    int toIdx = indexOfStation(stops2, to);

                    if (transferIdx2 != -1 && toIdx != -1 && transferIdx2 < toIdx) {
                        results.add(changeoverJourney(
                                s1, stops1, fromIdx, rs1,
                                s2, stops2, transferIdx2, toIdx,
                                transferStation
                        ));
                    }
                }
            }
        }

        if (results.isEmpty()) {
            throw new NotFoundException(
                    "No journeys found from " + from + " to " + to + " on " + date
            );
        }

        return results;
    }

    private JourneyOptionDto directJourney(
            Schedule s, List<RouteStop> stops, int fromIdx, int toIdx) {
        return new JourneyOptionDto(
                List.of(new JourneyLegDto(
                        s.getTrain().getName(),
                        stops.get(fromIdx).getStation().getName(),
                        stops.get(toIdx).getStation().getName(),
                        stops.get(fromIdx).getExpectedTime(),
                        stops.get(toIdx).getExpectedTime(),
                        s.getId()
                )),
                stops.get(fromIdx).getExpectedTime(),
                stops.get(toIdx).getExpectedTime()
        );
    }

    private JourneyOptionDto changeoverJourney(
            Schedule s1, List<RouteStop> stops1, int fromIdx, RouteStop transfer,
            Schedule s2, List<RouteStop> stops2, int transferIdx2, int toIdx,
            String transferStation) {
        return new JourneyOptionDto(
                List.of(
                        new JourneyLegDto(
                                s1.getTrain().getName(),
                                stops1.get(fromIdx).getStation().getName(),
                                transferStation,
                                stops1.get(fromIdx).getExpectedTime(),
                                transfer.getExpectedTime(),
                                s1.getId()
                        ),
                        new JourneyLegDto(
                                s2.getTrain().getName(),
                                transferStation,
                                stops2.get(toIdx).getStation().getName(),
                                stops2.get(transferIdx2).getExpectedTime(),
                                stops2.get(toIdx).getExpectedTime(),
                                s2.getId()
                        )
                ),
                stops1.get(fromIdx).getExpectedTime(),
                stops2.get(toIdx).getExpectedTime()
        );
    }

    private int indexOfStation(List<RouteStop> stops, String stationName) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStation().getName().equalsIgnoreCase(stationName))
                return i;
        }
        return -1;
    }
}