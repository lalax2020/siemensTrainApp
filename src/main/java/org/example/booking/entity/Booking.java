package org.example.booking.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.booking.BookingStatus;
import org.example.schedule.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String customerName;

    @NotNull
    private String customerEmail;

    @ManyToOne
    private Schedule schedule;

    private LocalDate travelDate;

    @Column(name = "number_of_seats")
    private int noOfSeats;

    @Column(name = "from_station")
    private String departureStation;

    @Column(name = "to_station")
    private String arrivalStation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(20)")
    private BookingStatus status;

    @Column(name = "booked_at")
    private LocalDateTime bookingDate;
}
