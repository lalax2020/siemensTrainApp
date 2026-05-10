package org.example.booking.dto;

import org.example.booking.BookingStatus;

import java.time.LocalDate;

public record BookingResponseDto(
        Long id,
        String customerName,
        String customerEmail,
        String fromStation,
        String toStation,
        LocalDate travelDate,
        int numberOfSeats,
        BookingStatus status
) {}