package org.example.booking.service;

import org.example.booking.BookingStatus;
import org.example.booking.dto.BookingRequestDto;
import org.example.booking.dto.BookingResponseDto;
import org.example.booking.entity.Booking;
import org.example.booking.repo.BookingRepository;
import org.example.infrastructure.exceptions.NotFoundException;
import org.example.infrastructure.exceptions.OverBookingException;
import org.example.infrastructure.services.EmailService;
import org.example.schedule.entity.Schedule;
import org.example.schedule.repo.ScheduleRepository;
import org.example.train.entity.Train;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class
BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock ScheduleRepository scheduleRepository;
    @Mock EmailService emailService;

    @InjectMocks BookingService bookingService;

    private Schedule schedule;
    private Train train;

    @BeforeEach
    void setUp() {
        train = new Train();
        train.setCapacity(100);

        schedule = new Schedule();
        schedule.setTrain(train);
    }

    @Test
    void create_happyPath_returnsDto() {
        BookingRequestDto req = new BookingRequestDto(
                "Alice", "alice@example.com", 1L,
                LocalDate.now(), 2, "Bucharest", "Cluj");

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepository.countBySchedule(schedule)).thenReturn(0);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(42L);
            b.setBookingDate(LocalDateTime.now());
            return b;
        });
        doNothing().when(emailService).sendBookingConfirmation(any());

        BookingResponseDto result = bookingService.create(req);

        assertThat(result.id()).isEqualTo(42L);
        assertThat(result.customerName()).isEqualTo("Alice");
        assertThat(result.numberOfSeats()).isEqualTo(2);
        assertThat(result.status()).isEqualTo(BookingStatus.ACTIVE);
        verify(emailService).sendBookingConfirmation(any());
    }

    @Test
    void create_overbooking_throwsOverBookingException() {
        train.setCapacity(5);
        BookingRequestDto req = new BookingRequestDto(
                "Bob", "bob@example.com", 1L,
                LocalDate.now(), 3, "Bucharest", "Cluj");

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepository.countBySchedule(schedule)).thenReturn(4);

        assertThatThrownBy(() -> bookingService.create(req))
                .isInstanceOf(OverBookingException.class)
                .hasMessageContaining("Not enough seats");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_scheduleNotFound_throwsNotFoundException() {
        BookingRequestDto req = new BookingRequestDto(
                "Carol", "carol@example.com", 99L,
                LocalDate.now(), 1, "A", "B");

        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Schedule not found");
    }

    @Test
    void cancel_happyPath_setsStatusCancelled() {
        Booking booking = Booking.builder()
                .id(10L).status(BookingStatus.ACTIVE)
                .customerName("Dan").customerEmail("dan@example.com")
                .noOfSeats(1).departureStation("A").arrivalStation("B")
                .travelDate(LocalDate.now()).bookingDate(LocalDateTime.now())
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.cancel(10L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancel_notFound_throwsNotFoundException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancel(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Booking not found");
    }
}
