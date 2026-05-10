package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.infrastructure.exceptions.NotFoundException;
import org.example.route.entity.Route;
import org.example.route.repository.RouteRepository;
import org.example.train.dto.TrainDto;
import org.example.train.entity.Train;
import org.example.train.repository.TrainRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainService {
    private final TrainRepository repo;
    private final RouteRepository routeRepo;

    @Cacheable(value = "trains", key = "#id", unless = "#result == null")
    public TrainDto getById(Long id) {
        Train t = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Train not found"));
        return toDto(t);
    }

    @Cacheable(value = "trains", key = "'all'")
    public List<TrainDto> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @CacheEvict(value = "trains", allEntries = true)
    public TrainDto update(TrainDto dto) {
        Train t = repo.findById(dto.id())
                .orElseThrow(() -> new NotFoundException("Train not found"));
        t.setName(dto.name());
        t.setCapacity(dto.capacity());
        if (dto.routeId() != null) {
            Route route = routeRepo.findById(dto.routeId())
                    .orElseThrow(() -> new NotFoundException("Route not found"));
            t.setRoute(route);
        }
        return toDto(repo.save(t));
    }

    @CacheEvict(value = "trains", allEntries = true)
    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }

    @CacheEvict(value = "trains",allEntries = true)
    public void deleteAll(){
        repo.deleteAll();
    }
    @CacheEvict(value = "trains", allEntries = true)
    public TrainDto create(TrainDto dto) {
        Train t = new Train();
        t.setName(dto.name());
        t.setCapacity(dto.capacity());
        if (dto.routeId() != null) {
            Route route = routeRepo.findById(dto.routeId())
                    .orElseThrow(() -> new NotFoundException("Route not found"));
            t.setRoute(route);
        }
        return toDto(repo.save(t));
    }

    public long count() {
        return repo.count();
    }

    private TrainDto toDto(Train t) {
        return new TrainDto(
                t.getId(),
                t.getName(),
                t.getCapacity(),
                t.getRoute() != null ? t.getRoute().getId() : null
        );
    }
}