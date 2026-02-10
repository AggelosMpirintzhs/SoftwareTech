package com.example.traineeship_app.Dao;

import com.example.traineeship_app.domainmodel.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentDao extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    Optional<Student> findByAm(String am);
    List<Student> findByLookingForTraineeshipTrue();
}

