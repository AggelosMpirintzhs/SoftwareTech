package com.example.traineeship_app.services.Impl;

import com.example.traineeship_app.Dao.CompanyDao;
import com.example.traineeship_app.Dao.EvaluationDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.Dto.CompanyDto;
import com.example.traineeship_app.Dto.TraineeshipPositionDto;
import com.example.traineeship_app.domainmodel.Company;
import com.example.traineeship_app.domainmodel.Evaluation;
import com.example.traineeship_app.domainmodel.EvaluationType;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.mappers.CompanyMapper;
import com.example.traineeship_app.mappers.TraineeshipPositionMapper;
import com.example.traineeship_app.services.CompanyService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyDao companyDao;
    private final TraineeshipPositionDao traineeshipPositionDao;
    private final EvaluationDao evaluationDao;

    public CompanyServiceImpl(CompanyDao companyDao,
                              TraineeshipPositionDao traineeshipPositionDao,
                              EvaluationDao evaluationDao) {
        this.companyDao = companyDao;
        this.traineeshipPositionDao = traineeshipPositionDao;
        this.evaluationDao = evaluationDao;
    }

    @Override
    public void createProfile(CompanyDto companyDTO, String username) {
        Company company = CompanyMapper.toEntity(companyDTO);
        company.setUsername(username);
        companyDao.save(company);
    }

    @Override
    public List<TraineeshipPosition> getAvailablePositions(String username) {
        return traineeshipPositionDao.findByCompany_Username(username);
    }


    @Override
    public void addTraineeshipPosition(String username, TraineeshipPositionDto dto) {
        TraineeshipPosition position = TraineeshipPositionMapper.toEntity(dto);
        Company company = companyDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        position.setCompany(company);
        traineeshipPositionDao.save(position);
    }

    @Override
    public void deleteTraineeship(Integer positionId) {
        traineeshipPositionDao.deleteById(positionId);
    }

    @Override
    public boolean hasProfile(String username) {
        return companyDao.findByUsername(username).isPresent();
    }

    @Override
    public Company getCompanyByUsername(String username) {
        return companyDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    @Override
    public void updateCompanyProfile(CompanyDto dto) {
        Company company = companyDao.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        company.setCompanyName(dto.getCompanyName());
        company.setCompanyLocation(dto.getCompanyLocation());
        companyDao.save(company);
    }

    @Override
    public Map<String, Object> getEvaluationData(Integer positionId) {
        TraineeshipPosition position = traineeshipPositionDao.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        Evaluation evaluation = findCompanyEvaluation(position);
        if (evaluation == null) evaluation = new Evaluation();

        Map<String, Object> data = new HashMap<>();
        data.put("position", position);
        data.put("evaluation", evaluation);
        return data;
    }

    @Override
    public void saveOrUpdateCompanyEvaluation(Integer positionId,
                                              Integer motivation,
                                              Integer efficiency,
                                              Integer effectiveness) {

        TraineeshipPosition position = traineeshipPositionDao.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        Evaluation evaluation = findCompanyEvaluation(position);

        if (evaluation == null) {
            evaluation = new Evaluation();
            evaluation.setEvaluationType(EvaluationType.COMPANY_EVALUATION);
            evaluation.setTraineeshipPosition(position);
            ensureEvaluationAttached(position, evaluation);
        }

        if (motivation != null) evaluation.setMotivation(motivation);
        if (efficiency != null) evaluation.setEfficiency(efficiency);
        if (effectiveness != null) evaluation.setEffectiveness(effectiveness);

        evaluationDao.save(evaluation);
    }

    private Evaluation findCompanyEvaluation(TraineeshipPosition position) {
        if (position.getEvaluations() != null) {
            for (Evaluation e : position.getEvaluations()) {
                if (e.getEvaluationType() == EvaluationType.COMPANY_EVALUATION) {
                    return e;
                }
            }
        }
        return null;
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
