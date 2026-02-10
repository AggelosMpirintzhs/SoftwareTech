package com.example.traineeship_app.services;

import com.example.traineeship_app.Dao.UserDao;
import com.example.traineeship_app.domainmodel.User;
import com.example.traineeship_app.services.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser_EncodesPasswordAndSaves() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plainpassword");

        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedPassword");

        userService.saveUser(user);

        assertEquals("encodedPassword", user.getPassword());
        verify(userDao).save(user);
    }

    @Test
    void testIsUserPresent_WhenExists() {
        User user = new User();
        user.setUsername("existingUser");

        //username is in lowerCase because we use normalized usernames
        when(userDao.findByUsername("existingUser".trim().toLowerCase())).thenReturn(Optional.of(user));
        boolean result = userService.isUserPresent(user);
        assertTrue(result);
    }

    @Test
    void testIsUserPresent_WhenNotExists() {
        User user = new User();
        user.setUsername("missingUser");

        when(userDao.findByUsername("missingUser")).thenReturn(Optional.empty());

        boolean result = userService.isUserPresent(user);
        assertFalse(result);
    }

    @Test
    void testFindUserByUsername_WhenExists() {
        User user = new User();
        user.setUsername("john");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.findUserByUsername("john");

        assertEquals("john", result.getUsername());
    }

    @Test
    void testFindUserByUsername_WhenNotExists_ThrowsException() {
        when(userDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findUserByUsername("ghost");
        });
    }

    @Test
    void testLoadUserByUsername_WhenExists() {
        User user = new User();
        user.setUsername("john");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        assertEquals(user, userService.loadUserByUsername("john"));
    }

    @Test
    void testLoadUserByUsername_WhenNotExists_ThrowsException() {
        when(userDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("missing");
        });
    }

    @Test
    void testFindById_WhenExists() {
        User user = new User();
        user.setUsername("john");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById("john");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    @Test
    void testFindById_WhenNotExists_ThrowsException() {
        when(userDao.findByUsername("nope")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findById("nope");
        });
    }
}
