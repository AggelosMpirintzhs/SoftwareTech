package com.example.traineeship_app.Dao;

import com.example.traineeship_app.domainmodel.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfessorDao extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUsername(String username);
}
