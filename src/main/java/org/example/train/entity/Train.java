package org.example.train.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.example.route.entity.Route;

@Entity
@Table(name = "trains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Train{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer capacity;

    @ManyToOne
    private Route route;
}
