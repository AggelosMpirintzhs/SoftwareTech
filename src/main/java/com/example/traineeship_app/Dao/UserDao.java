package com.example.traineeship_app.Dao;

import com.example.traineeship_app.domainmodel.Role;
import com.example.traineeship_app.domainmodel.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAllByRole(Role role);

}
