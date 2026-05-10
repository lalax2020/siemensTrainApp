package org.example.admin.controller;

import org.example.admin.service.AdminService;
import org.example.booking.dto.BookingResponseDto;
import org.example.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.example.route.dto.RouteDto;
import org.example.route.entity.Route;
import org.example.routestop.dto.RouteStopCreateDto;
import org.example.routestop.dto.RouteStopDto;
import org.example.schedule.dto.ScheduleDto;
import org.example.schedule.entity.Schedule;
import org.example.station.dto.StationDto;
import org.example.station.entity.Station;
import org.example.train.dto.TrainDto;
import org.example.train.entity.Train;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final BookingService bookingService;

    // ── Trains ───────────────────────────────────────────────────────────────

    @PostMapping("/trains")
    public ResponseEntity<Train> createTrain(@RequestBody TrainDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createTrain(dto));
    }

    @DeleteMapping("/trains/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        adminService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }

    // ── Routes ───────────────────────────────────────────────────────────────

    @GetMapping("/routes")
    public ResponseEntity<List<Route>> listRoutes() {
        return ResponseEntity.ok(adminService.listRoutes());
    }

    @PostMapping("/routes")
    public ResponseEntity<Route> createRoute(@RequestBody RouteDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createRoute(dto));
    }

    @PutMapping("/routes/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody RouteDto dto) {
        return ResponseEntity.ok(adminService.updateRoute(id, dto));
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        adminService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    // ── Route Stops ───────────────────────────────────────────────────────────

    @PostMapping("/route-stops")
    public ResponseEntity<RouteStopDto> addRouteStop(@RequestBody RouteStopCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addRouteStop(dto));
    }

    @DeleteMapping("/route-stops/{id}")
    public ResponseEntity<Void> deleteRouteStop(@PathVariable Long id) {
        adminService.deleteRouteStop(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/route-stops/{id}/actual-time")
    public ResponseEntity<Void> updateActualTime(
            @PathVariable Long id,
            @RequestParam String actualTime) {
        adminService.updateActualTime(id, LocalTime.parse(actualTime));
        return ResponseEntity.noContent().build();
    }

    // ── Stations ─────────────────────────────────────────────────────────────

    @GetMapping("/stations")
    public ResponseEntity<List<Station>> listStations() {
        return ResponseEntity.ok(adminService.listStations());
    }

    @PostMapping("/stations")
    public ResponseEntity<Station> createStation(@RequestBody StationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createStation(dto));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        adminService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    // ── Schedules ─────────────────────────────────────────────────────────────

    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> listSchedules() {
        return ResponseEntity.ok(adminService.listSchedules());
    }

    @PostMapping("/schedules")
    public ResponseEntity<Schedule> createSchedule(@RequestBody ScheduleDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createSchedule(dto));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        adminService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    // ── Bookings ──────────────────────────────────────────────────────────────

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDto>> getBookings(
            @RequestParam Long scheduleId,
            @RequestParam LocalDate date) {
        return ResponseEntity.ok(bookingService.getByScheduleAndDate(scheduleId, date));
    }
}
