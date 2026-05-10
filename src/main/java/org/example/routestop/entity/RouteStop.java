package org.example.routestop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.route.entity.Route;
import org.example.station.entity.Station;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@Table(name = "route_stops")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Route route;

    @ManyToOne
    private Station station;

    private int stopOrder;
    private LocalTime expectedTime;
    private LocalTime actualTime;

    public Integer getDelayMinutes(){
        if(actualTime == null) return null;
        int diff = (int) ChronoUnit.MINUTES.between(expectedTime, actualTime);
        return diff > 0 ? diff : 0;
    }
}
