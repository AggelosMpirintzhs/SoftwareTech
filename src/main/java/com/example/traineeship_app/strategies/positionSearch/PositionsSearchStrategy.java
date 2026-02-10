package com.example.traineeship_app.strategies.positionSearch;

import com.example.traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;

public interface PositionsSearchStrategy {
    List<TraineeshipPosition> search(String applicantUsername);
}