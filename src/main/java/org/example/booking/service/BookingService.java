package org.example.booking.service;


import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final EmailService emailService;

    @Transactional
    public BookingResponseDto create(BookingRequestDto dto){
        Schedule schedule = scheduleRepository.findById(dto.scheduleId())
                .orElseThrow(()->new NotFoundException("Schedule not found"));

        int booked = bookingRepository.countBySchedule(schedule);//vezi de travel date
        int capacity = schedule.getTrain().getCapacity();

        if(booked + dto.numberOfSeats() > capacity){
            throw new OverBookingException("Not enough seats available");
        }

        Booking booking = Booking.builder()
                .customerName(dto.customerName())
                .customerEmail(dto.customerEmail())
                .schedule(schedule)
                .travelDate(dto.travelDate())
                .noOfSeats(dto.numberOfSeats())
                .departureStation(dto.fromStation())
                .arrivalStation(dto.toStation())
                .status(BookingStatus.ACTIVE)
                .bookingDate(LocalDateTime.now())
                .build();

        Booking saved = bookingRepository.save(booking);
        emailService.sendBookingConfirmation(saved);
        return toDto(saved);
    }

    public void cancel(Long id){
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public List<BookingResponseDto> getByScheduleAndDate(Long scheduleId, LocalDate date){
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(()-> new NotFoundException("Schedule not found"));
        return bookingRepository.findByScheduleAndTravelDate(schedule,date)
                .stream().map(this::toDto).toList();
    }

    private BookingResponseDto toDto(Booking b){
        return new BookingResponseDto(
            b.getId(), b.getCustomerName(), b.getCustomerEmail(),
            b.getDepartureStation(), b.getArrivalStation(),
            b.getTravelDate(), b.getNoOfSeats(),  b.getStatus()
        );
    }
}
