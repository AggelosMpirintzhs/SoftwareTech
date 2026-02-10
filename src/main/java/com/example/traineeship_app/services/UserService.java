package com.example.traineeship_app.services;

import com.example.traineeship_app.domainmodel.User;

import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    boolean isUserPresent(User user);
    Optional<User> findById(String username);

}