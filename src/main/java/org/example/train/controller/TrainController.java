package org.example.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.train.dto.DeleteResult;
import org.example.train.dto.TrainDto;
import org.example.train.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
@RequiredArgsConstructor
public class TrainController {
    private final TrainService service;

    @GetMapping
    public List<TrainDto> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<TrainDto> create(@Valid @RequestBody TrainDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainDto> update(@PathVariable Long id, @Valid @RequestBody TrainDto dto) {
        if (!id.equals(dto.id())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id Mismatch");
        }
        return ResponseEntity.ok(service.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResult> delete(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        if (deleted) {
            return ResponseEntity.ok(new DeleteResult(id, true));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }
}