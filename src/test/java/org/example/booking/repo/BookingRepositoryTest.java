package org.example.booking.repo;

import org.example.booking.BookingStatus;
import org.example.booking.entity.Booking;
import org.example.route.entity.Route;
import org.example.schedule.entity.Schedule;
import org.example.station.entity.Station;
import org.example.train.entity.Train;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired BookingRepository bookingRepository;

    @Test
    void findByScheduleAndTravelDate_returnsOnlyMatchingBookings() {
        Route route = em.persist(Route.builder().name("Route A").build());
        Train train = em.persist(Train.builder().name("IC 1").capacity(100).route(route).build());
        Schedule schedule = new Schedule();
        schedule.setTrain(train);
        em.persist(schedule);

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Booking b1 = booking(schedule, today);
        Booking b2 = booking(schedule, tomorrow);
        em.persist(b1);
        em.persist(b2);
        em.flush();

        List<Booking> result = bookingRepository.findByScheduleAndTravelDate(schedule, today);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTravelDate()).isEqualTo(today);
    }

    @Test
    void countBySchedule_returnsCorrectCount() {
        Route route = em.persist(Route.builder().name("Route B").build());
        Train train = em.persist(Train.builder().name("IC 2").capacity(50).route(route).build());
        Schedule schedule = new Schedule();
        schedule.setTrain(train);
        em.persist(schedule);

        em.persist(booking(schedule, LocalDate.now()));
        em.persist(booking(schedule, LocalDate.now()));
        em.flush();

        assertThat(bookingRepository.countBySchedule(schedule)).isEqualTo(2);
    }

    private Booking booking(Schedule schedule, LocalDate date) {
        return Booking.builder()
                .customerName("Test User")
                .customerEmail("test@example.com")
                .schedule(schedule)
                .travelDate(date)
                .noOfSeats(1)
                .departureStation("A")
                .arrivalStation("B")
                .status(BookingStatus.ACTIVE)
                .bookingDate(LocalDateTime.now())
                .build();
    }
}
