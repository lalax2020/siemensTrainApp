package org.example.booking.dto;

public record BookingDto(String customerName,
                         String customerEmail,
                         Long scheduleId,
                         int numberOfSeats, String departureStation, String arrivalStation) {
}
