package com.example.traineeship_app.strategies.supervisorSearch;

import com.example.traineeship_app.Dto.ProfessorMatchDto;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;

public interface SupervisorAssignmentStrategy {
    List<ProfessorMatchDto> findSuitableProfessors(TraineeshipPosition position);
}
