package com.example.traineeship_app.services.Impl;

import com.example.traineeship_app.Dao.StudentDao;
import com.example.traineeship_app.Dto.StudentDto;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.mappers.StudentMapper;
import com.example.traineeship_app.services.StudentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;

    public StudentServiceImpl(StudentDao studentMapper) {
        this.studentDao = studentMapper;
    }

    @Override
    public void saveProfile(StudentDto studentdto, String username) {
        Student student = StudentMapper.toEntity(studentdto);
        student.setUsername(username);
        studentDao.save(student);
    }

    @Override
    public Student getStudentByAm(String am) {
        return studentDao.findByAm(am).orElse(null);
    }

    @Override
    public void updateStudentProfile(StudentDto dto) {
        Student student = studentDao.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setStudentName(dto.getStudentName());
        student.setAm(dto.getAm());
        student.setPreferredLocation(dto.getPreferredLocation());
        student.setInterests(dto.getInterests());
        student.setSkills(dto.getSkills());
        studentDao.save(student);
    }

    @Override
    public Student retrieveProfile(String studentUsername) {
        return studentDao.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Override
    public void updateLogbook(String username, String logbookText) {
        Student student = studentDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        TraineeshipPosition position = student.getAssignedTraineeship();
        if (position == null) {
            throw new RuntimeException("No assigned traineeship");
        }
        position.setStudentLogbook(logbookText);
        student.setAssignedTraineeship(position);
        studentDao.save(student);
    }

    @Override
    public boolean hasProfile(String username) {
        return studentDao.findByUsername(username).isPresent();
    }

    @Override
    public Student getStudentByUsername(String username) {
        return studentDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Override
    public void setLookingForTraineeshipTrue(String username) {
        Student student = studentDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setLookingForTraineeship(true);
        studentDao.save(student);
    }
}
