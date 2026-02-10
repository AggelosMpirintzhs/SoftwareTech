package com.example.traineeship_app.services;

import com.example.traineeship_app.Dto.PositionWithScoreDto;
import com.example.traineeship_app.Dto.ProfessorMatchDto;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;
import java.util.Map;

public interface CommitteeService {
    List<Student> retrieveTraineeshipApplicants();
    List<PositionWithScoreDto> getScoredPositionsForApplicant(Integer studentId, String strategy);
    void assignPositionToStudent(Integer studentId, Integer positionId);
    List<TraineeshipPosition> getAssignedInProgressPositions();
    List<Boolean> getCompletableFlags(List<TraineeshipPosition> positions);
    Map<Integer, Boolean> getProfessorEvalFlags(List<TraineeshipPosition> positions);
    Map<Integer, Boolean> getCompanyEvalFlags(List<TraineeshipPosition> positions);
    List<ProfessorMatchDto> findSuitableProfessors(int positionId, String strategyName);
    boolean assignProfessorToPosition(int positionId, int professorId);
    void completeTraineeship(Integer positionId);
}
