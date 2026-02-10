package com.example.traineeship_app.Dao;

import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeshipPositionDao extends JpaRepository<TraineeshipPosition, Integer> {
    List<TraineeshipPosition> findByCompany_Username(String username);
    List<TraineeshipPosition> findByCompany_UsernameAndIsAssignedTrue(String username);
    List<TraineeshipPosition> findAll();
//    List<TraineeshipPosition> findByCompany_CompanyLocation(String location);
}