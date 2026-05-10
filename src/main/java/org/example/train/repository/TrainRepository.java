package org.example.train.repository;

import org.example.route.entity.Route;
import org.example.train.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {
    List<Train> findByRoute(Route route);
}
