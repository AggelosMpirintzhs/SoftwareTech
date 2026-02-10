package com.example.traineeship_app.services;

import com.example.traineeship_app.Dao.StudentDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.Dto.StudentDto;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.services.Impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceImplTest {

    @Mock
    private StudentDao studentDao;

    @Mock
    private TraineeshipPositionDao positionDao;

    @InjectMocks
    private StudentServiceImpl studentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveProfile_CallsSave() {
        StudentDto dto = new StudentDto("user", "John", "12345", "Athens", List.of("Java"), List.of("Spring"));
        studentService.saveProfile(dto, "user");
        verify(studentDao).save(any(Student.class));
    }

    @Test
    void testRetrieveProfile_WhenExists() {
        Student student = new Student();
        student.setUsername("user");

        when(studentDao.findByUsername("user")).thenReturn(Optional.of(student));

        Student result = studentService.retrieveProfile("user");

        assertEquals("user", result.getUsername());
    }

    @Test
    void testRetrieveProfile_WhenNotFound_Throws() {
        when(studentDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            studentService.retrieveProfile("ghost");
        });
    }

    @Test
    void testUpdateLogbook_Success() {
        Student student = new Student();
        TraineeshipPosition position = new TraineeshipPosition();
        student.setAssignedTraineeship(position);

        when(studentDao.findByUsername("user")).thenReturn(Optional.of(student));

        studentService.updateLogbook("user", "My logbook entry");

        assertEquals("My logbook entry", position.getStudentLogbook());
        verify(studentDao).save(student);
    }

    @Test
    void testUpdateLogbook_WhenNoPosition_Throws() {
        Student student = new Student(); // no assigned traineeship

        when(studentDao.findByUsername("user")).thenReturn(Optional.of(student));

        assertThrows(RuntimeException.class, () -> {
            studentService.updateLogbook("user", "entry");
        });
    }

    @Test
    void testHasProfile_WhenExists() {
        when(studentDao.findByUsername("user")).thenReturn(Optional.of(new Student()));
        assertTrue(studentService.hasProfile("user"));
    }

    @Test
    void testHasProfile_WhenNotExists() {
        when(studentDao.findByUsername("ghost")).thenReturn(Optional.empty());
        assertFalse(studentService.hasProfile("ghost"));
    }

    @Test
    void testGetStudentByUsername_ReturnsStudent() {
        Student student = new Student();
        student.setUsername("student1");

        when(studentDao.findByUsername("student1")).thenReturn(Optional.of(student));

        Student result = studentService.getStudentByUsername("student1");

        assertEquals("student1", result.getUsername());
    }

    @Test
    void testUpdateStudentProfile_UpdatesAndSaves() {
        Student student = new Student();
        student.setUsername("user");

        StudentDto dto = new StudentDto("user", "Updated Name", "2023001", "Thessaloniki",
                List.of("AI"), List.of("Python"));

        when(studentDao.findByUsername("user")).thenReturn(Optional.of(student));

        studentService.updateStudentProfile(dto);

        assertEquals("Updated Name", student.getStudentName());
        assertEquals("2023001", student.getAm());
        assertEquals("Thessaloniki", student.getPreferredLocation());
        assertEquals(List.of("AI"), student.getInterests());
        assertEquals(List.of("Python"), student.getSkills());
        verify(studentDao).save(student);
    }

    @Test
    void testSetLookingForTraineeshipTrue_SetsFlagAndSaves() {
        Student student = new Student();
        student.setUsername("user");
        student.setLookingForTraineeship(false);

        when(studentDao.findByUsername("user")).thenReturn(Optional.of(student));

        studentService.setLookingForTraineeshipTrue("user");

        assertTrue(student.isLookingForTraineeship());
        verify(studentDao).save(student);
    }
}
