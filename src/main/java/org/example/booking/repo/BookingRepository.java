package org.example.booking.repo;

import org.example.booking.entity.Booking;
import org.example.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findByScheduleAndTravelDate(Schedule schedule, LocalDate travelDate);
    int countBySchedule(Schedule schedule);
}
