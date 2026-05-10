package org.example.station.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "stations")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Station {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String name;
   private String city;
}
