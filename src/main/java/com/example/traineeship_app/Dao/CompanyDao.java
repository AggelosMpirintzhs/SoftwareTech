package com.example.traineeship_app.Dao;

import com.example.traineeship_app.domainmodel.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompanyDao extends JpaRepository<Company, Long> {
    Optional<Company> findByUsername(String username);
}
