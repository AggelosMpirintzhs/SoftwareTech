package com.example.traineeship_app.mappers;

import com.example.traineeship_app.domainmodel.Professor;
import com.example.traineeship_app.Dto.ProfessorDto;


public class ProfessorMapper {

    public static ProfessorDto toDto(Professor professor) {
        ProfessorDto dto = new ProfessorDto();
        dto.setUsername(professor.getUsername());
        dto.setProfessorName(professor.getProfessorName());
        dto.setInterests(professor.getInterests());
        return dto;
    }

    public static Professor toEntity(ProfessorDto dto) {
        Professor professor = new Professor();
        professor.setUsername(dto.getUsername());
        professor.setProfessorName(dto.getProfessorName());
        professor.setInterests(dto.getInterests());
        return professor;
    }
}
