package com.example.traineeship_app.services;

import com.example.traineeship_app.Dto.StudentDto;
import com.example.traineeship_app.domainmodel.Student;

public interface StudentService {
    void saveProfile(StudentDto studentdto, String username);
    Student retrieveProfile(String studentUsername);
    void updateLogbook(String username, String logbookText);
    boolean hasProfile(String username);
    Student getStudentByUsername(String username);
    void updateStudentProfile(StudentDto dto);
    void setLookingForTraineeshipTrue(String username);
    Student getStudentByAm(String am);
}
