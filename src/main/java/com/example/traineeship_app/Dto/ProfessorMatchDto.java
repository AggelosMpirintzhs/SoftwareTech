package com.example.traineeship_app.Dto;

import com.example.traineeship_app.domainmodel.Professor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfessorMatchDto {
    private Professor professor;
    private int matchedInterests;
    private int totalPositionInterests;
    private int load;
}
