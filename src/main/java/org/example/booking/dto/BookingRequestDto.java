package org.example.booking.dto;

import java.time.LocalDate;

public record BookingRequestDto(
        String customerName,
        String customerEmail,
        Long scheduleId,
        LocalDate travelDate,
        int numberOfSeats,
        String fromStation,
        String toStation
) {}