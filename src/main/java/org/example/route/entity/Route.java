package org.example.route.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.routestop.entity.RouteStop;

import java.util.List;

@Entity
@Table(name = "routes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    @OrderBy("stopOrder ASC")
    private List<RouteStop> stops;

}
