package com.example.traineeship_app.services;

import com.example.traineeship_app.Dto.CompanyDto;
import com.example.traineeship_app.Dto.TraineeshipPositionDto;
import com.example.traineeship_app.domainmodel.Company;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import java.util.List;
import java.util.Map;

public interface CompanyService {

    void createProfile(CompanyDto companyDTO, String username); // US7
    List<TraineeshipPosition> getAvailablePositions(String username); // US8
    void addTraineeshipPosition(String username, TraineeshipPositionDto dto); // US10
    void deleteTraineeship(Integer positionId); // US11
    boolean hasProfile(String username);
    Company getCompanyByUsername(String username);
    void saveOrUpdateCompanyEvaluation(Integer positionId, Integer motivation, Integer efficiency, Integer effectiveness);
    void updateCompanyProfile(CompanyDto dto);
    Map<String, Object> getEvaluationData(Integer positionId);

}