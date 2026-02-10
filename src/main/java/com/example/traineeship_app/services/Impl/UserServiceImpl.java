package com.example.traineeship_app.services.Impl;

import com.example.traineeship_app.domainmodel.User;

import java.util.Optional;

import com.example.traineeship_app.Dao.UserDao;
import com.example.traineeship_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserDao userDao;

    public User findUserByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public void saveUser(User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userDao.save(user);
    }

    @Override
    public boolean isUserPresent(User user) {
        String normalizedUsername = user.getUsername().trim().toLowerCase();
        Optional<User> storedUser = userDao.findByUsername(normalizedUsername);
        return storedUser.isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException(
                        String.format("USER_NOT_FOUND", username)
                ));
    }

    public Optional<User> findById(String username) {
        return Optional.ofNullable(userDao.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("USER_NOT_FOUND", username)
                )));
    }

}
