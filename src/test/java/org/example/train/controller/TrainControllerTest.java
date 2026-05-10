package org.example.train.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.auth.security.JwtUtils;
import org.example.infrastructure.exceptions.NotFoundException;
import org.example.train.dto.TrainDto;
import org.example.train.service.TrainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    value = TrainController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class TrainControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean TrainService trainService;
    // Mock JwtUtils and UserDetailsService so JwtFilter can be created,
    // but requests without a Bearer header pass straight through.
    @MockBean JwtUtils jwtUtils;
    @MockBean org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    void getAll_returnsList() throws Exception {
        when(trainService.getAll()).thenReturn(List.of(
                new TrainDto(1L, "IC 123", 200, null),
                new TrainDto(2L, "RE 456", 150, 1L)
        ));

        mockMvc.perform(get("/api/trains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("IC 123"));
    }

    @Test
    void getById_found_returnsTrain() throws Exception {
        when(trainService.getById(1L)).thenReturn(new TrainDto(1L, "IC 123", 200, null));

        mockMvc.perform(get("/api/trains/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.capacity").value(200));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(trainService.getById(99L)).thenThrow(new NotFoundException("Train not found"));

        mockMvc.perform(get("/api/trains/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validBody_returns201() throws Exception {
        TrainDto dto = new TrainDto(null, "IC 789", 180, null);
        TrainDto saved = new TrainDto(3L, "IC 789", 180, null);
        when(trainService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/trains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void update_idMismatch_returns400() throws Exception {
        TrainDto dto = new TrainDto(5L, "X", 100, null);

        mockMvc.perform(put("/api/trains/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_exists_returns200() throws Exception {
        when(trainService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/trains/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.succes").value(true));
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        when(trainService.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/trains/99"))
                .andExpect(status().isNotFound());
    }
}
