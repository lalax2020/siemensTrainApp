package org.example.booking.controller;

import lombok.RequiredArgsConstructor;
import org.example.booking.dto.BookingRequestDto;
import org.example.booking.dto.BookingResponseDto;
import org.example.booking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto dto){
        return ResponseEntity.ok(bookingService.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id){
        bookingService.cancel(id);
        return ResponseEntity.noContent().build();
    }

}
