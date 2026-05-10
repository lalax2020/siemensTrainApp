package org.example.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.booking.entity.Booking;
import org.example.booking.repo.BookingRepository;
import org.example.infrastructure.exceptions.NotFoundException;
import org.example.infrastructure.services.EmailService;
import org.example.route.dto.RouteDto;
import org.example.route.entity.Route;
import org.example.route.repository.RouteRepository;
import org.example.routestop.dto.RouteStopCreateDto;
import org.example.routestop.dto.RouteStopDto;
import org.example.routestop.entity.RouteStop;
import org.example.routestop.repo.RouteStopRepository;
import org.example.schedule.dto.ScheduleDto;
import org.example.schedule.entity.Schedule;
import org.example.schedule.repo.ScheduleRepository;
import org.example.station.dto.StationDto;
import org.example.station.entity.Station;
import org.example.station.repository.StationRepository;
import org.example.train.dto.TrainDto;
import org.example.train.entity.Train;
import org.example.train.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final TrainRepository trainRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final StationRepository stationRepository;

    // ── Trains ──────────────────────────────────────────────────────────────

    public Train createTrain(TrainDto dto) {
        Train t = new Train();
        t.setName(dto.name());
        t.setCapacity(dto.capacity());
        return trainRepository.save(t);
    }

    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
    }

    // ── Routes ──────────────────────────────────────────────────────────────

    public List<Route> listRoutes() {
        return routeRepository.findAll();
    }

    @Transactional
    public Route createRoute(RouteDto routeDto) {
        Route route = new Route();
        route.setName(routeDto.name());
        return routeRepository.save(route);
    }

    @Transactional
    public Route updateRoute(Long id, RouteDto dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route not found"));
        route.setName(dto.name());
        return routeRepository.save(route);
    }

    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    // ── Route Stops ──────────────────────────────────────────────────────────

    @Transactional
    public RouteStopDto addRouteStop(RouteStopCreateDto dto) {
        Route route = routeRepository.findById(dto.routeId())
                .orElseThrow(() -> new NotFoundException("Route not found"));
        Station station = stationRepository.findById(dto.stationId())
                .orElseThrow(() -> new NotFoundException("Station not found"));

        RouteStop stop = RouteStop.builder()
                .route(route)
                .station(station)
                .stopOrder(dto.stopOrder())
                .expectedTime(dto.expectedTime())
                .build();
        RouteStop saved = routeStopRepository.save(stop);
        return new RouteStopDto(
                saved.getId(),
                saved.getStation().getName(),
                saved.getStopOrder(),
                saved.getExpectedTime(),
                saved.getActualTime(),
                saved.getDelayMinutes()
        );
    }

    public void deleteRouteStop(Long id) {
        routeStopRepository.deleteById(id);
    }

    @Transactional
    public void updateActualTime(Long routeStopId, LocalTime actualTime) {
        RouteStop stop = routeStopRepository.findById(routeStopId)
                .orElseThrow(() -> new NotFoundException("RouteStop not found"));

        stop.setActualTime(actualTime);
        routeStopRepository.save(stop);

        Integer delay = stop.getDelayMinutes();
        if (delay == null || delay == 0) return;

        LocalDate today = LocalDate.now();
        List<Train> trains = trainRepository.findByRoute(stop.getRoute());

        for (Train train : trains) {
            List<Schedule> schedules = scheduleRepository.findByTrain(train);
            for (Schedule schedule : schedules) {
                if (schedule.getDays().contains(today.getDayOfWeek())) {
                    List<Booking> affected = bookingRepository.findByScheduleAndTravelDate(schedule, today);
                    for (Booking b : affected) {
                        try {
                            emailService.sendDelayNotification(b, stop.getStation().getName(), delay);
                        } catch (Exception e) {
                            System.err.println("[AdminService] Delay email failed for " + b.getCustomerEmail() + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    // ── Stations ─────────────────────────────────────────────────────────────

    public List<Station> listStations() {
        return stationRepository.findAll();
    }

    public Station createStation(StationDto dto) {
        Station station = Station.builder()
                .name(dto.name())
                .city(dto.city())
                .build();
        return stationRepository.save(station);
    }

    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
    }

    // ── Schedules ────────────────────────────────────────────────────────────

    public List<Schedule> listSchedules() {
        return scheduleRepository.findAll();
    }

    @Transactional
    public Schedule createSchedule(ScheduleDto dto) {
        Train train = trainRepository.findById(dto.trainId())
                .orElseThrow(() -> new NotFoundException("Train not found"));
        Schedule schedule = new Schedule();
        schedule.setTrain(train);
        schedule.setDays(dto.daysOfWeek());
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
