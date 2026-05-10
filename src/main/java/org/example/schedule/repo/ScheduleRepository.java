package org.example.schedule.repo;

import org.example.schedule.entity.Schedule;
import org.example.train.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    List<Schedule> findScheduleByDays(DayOfWeek day);
    List<Schedule> findByTrain(Train train);
}
