package com.example.traineeship_app.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDto {
    private String username;
    private String professorName;
    private List<String> interests;
    private List<TraineeshipPositionDto> supervisedPositions;

    public ProfessorDto(String username,String ProfessorName,List<String> interests){
        this.username = username;
        this.professorName = ProfessorName;
        this.interests = interests;
    }
}
