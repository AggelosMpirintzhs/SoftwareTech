package com.example.traineeship_app.services.Impl;

import com.example.traineeship_app.Dao.EvaluationDao;
import com.example.traineeship_app.Dao.ProfessorDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.Dto.ProfessorDto;
import com.example.traineeship_app.domainmodel.*;
import com.example.traineeship_app.mappers.ProfessorMapper;
import com.example.traineeship_app.services.ProfessorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorDao professorDao;
    private final TraineeshipPositionDao traineeshipPositionDao;
    private final EvaluationDao evaluationDao;

    public ProfessorServiceImpl(ProfessorDao professorDao, TraineeshipPositionDao traineeshipPositionDao, EvaluationDao evaluationDao) {
        this.professorDao = professorDao;
        this.traineeshipPositionDao = traineeshipPositionDao;
        this.evaluationDao = evaluationDao;
    }

    @Override
    public void createProfile(ProfessorDto professorDTO, String username) {
        Professor professor = ProfessorMapper.toEntity(professorDTO);
        professorDao.save(professor);
    }


    @Override
    public boolean hasProfile(String username) {
        return professorDao.findByUsername(username).isPresent();
    }

    @Override
    public Professor getProfessorByUsername(String username) {
        return professorDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Professor not found"));
    }

    @Override
    public void updateProfessorProfile(ProfessorDto professorDto) {
        Professor professor = getProfessorByUsername(professorDto.getUsername());
        professor.setProfessorName(professorDto.getProfessorName());
        professor.setInterests(professorDto.getInterests());
        professorDao.save(professor);
    }


    @Override
    public Map<String, Object> getEvaluationData(Integer positionId) {
        TraineeshipPosition position = traineeshipPositionDao.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        Evaluation evaluation = findOrCreateProfessorEvaluation(position);

        Map<String, Object> data = new HashMap<>();
        data.put("position", position);
        data.put("evaluation", evaluation);

        return data;
    }

    @Override
    public void saveOrUpdateProfessorEvaluation(Integer positionId,
                                                Integer motivation,
                                                Integer efficiency,
                                                Integer effectiveness,
                                                Integer facilities,
                                                Integer guidance) {

        TraineeshipPosition position = traineeshipPositionDao.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        Evaluation evaluation = findOrCreateProfessorEvaluation(position);

        if (motivation != null)     evaluation.setMotivation(motivation);
        if (efficiency != null)     evaluation.setEfficiency(efficiency);
        if (effectiveness != null)  evaluation.setEffectiveness(effectiveness);
        if (facilities != null)     evaluation.setFacilities(facilities);
        if (guidance != null)       evaluation.setGuidance(guidance);

        evaluationDao.save(evaluation);
    }
    private Evaluation findOrCreateProfessorEvaluation(TraineeshipPosition position) {
        if (position.getEvaluations() != null) {
            for (Evaluation e : position.getEvaluations()) {
                if (e.getEvaluationType() == EvaluationType.PROFESSOR_EVALUATION) {
                    return e;
                }
            }
        }

        Evaluation newEval = new Evaluation();
        newEval.setEvaluationType(EvaluationType.PROFESSOR_EVALUATION);
        newEval.setTraineeshipPosition(position);
        ensureEvaluationAttached(position, newEval);
        return newEval;
    }

    private void ensureEvaluationAttached(TraineeshipPosition position, Evaluation evaluation) {
        if (position.getEvaluations() == null) {
            position.setEvaluations(new ArrayList<>());
        }
        if (!position.getEvaluations().contains(evaluation)) {
            position.getEvaluations().add(evaluation);
            evaluation.setTraineeshipPosition(position);
        }
    }
}
