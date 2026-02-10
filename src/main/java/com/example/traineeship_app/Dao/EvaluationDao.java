package com.example.traineeship_app.Dao;


import com.example.traineeship_app.domainmodel.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationDao extends JpaRepository<Evaluation, Integer> {
    Optional<Evaluation> findByTraineeshipPosition_Id(Integer positionId);
}