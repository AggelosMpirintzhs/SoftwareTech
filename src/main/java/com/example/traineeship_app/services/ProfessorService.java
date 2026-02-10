package com.example.traineeship_app.services;

import com.example.traineeship_app.Dto.ProfessorDto;
import com.example.traineeship_app.domainmodel.Professor;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;
import java.util.Map;

public interface ProfessorService {

    void createProfile(ProfessorDto professorDTO, String username);
    boolean hasProfile(String username);
    Professor getProfessorByUsername(String username);
    void updateProfessorProfile(ProfessorDto professorDto);
    void saveOrUpdateProfessorEvaluation(Integer positionId, Integer motivation, Integer efficiency, Integer effectiveness, Integer facilities, Integer guidance);
    Map<String, Object> getEvaluationData(Integer positionId);
}
