package com.example.traineeship_app.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeshipPositionDto {
    private String title;
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String topics;
    private String skills;
    private Boolean isAssigned;

}

