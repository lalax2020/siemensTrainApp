package org.example.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.example.infrastructure.services.SearchService;
import org.example.journey.JourneyOptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
   private final SearchService searchService;

   @GetMapping
   public ResponseEntity<List<JourneyOptionDto>> search(@RequestParam String departure,
                                                        @RequestParam String arrival,
                                                        @RequestParam LocalDate date){
       return ResponseEntity.ok(searchService.findJourneys(departure, arrival, date));
   }
}
