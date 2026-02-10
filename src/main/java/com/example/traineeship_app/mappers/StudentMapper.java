package com.example.traineeship_app.mappers;

import com.example.traineeship_app.Dto.StudentDto;
import com.example.traineeship_app.domainmodel.Student;

public class StudentMapper {

    public static StudentDto toDto(Student student) {
        StudentDto dto = new StudentDto();
        dto.setUsername(student.getUsername());
        dto.setStudentName(student.getStudentName());
        dto.setAm(student.getAm());
        dto.setAvgGrade(student.getAvgGrade());
        dto.setPreferredLocation(student.getPreferredLocation());
        dto.setInterests(student.getInterests());
        dto.setSkills(student.getSkills());
        dto.setLookingForTraineeship(student.isLookingForTraineeship());
        return dto;
    }

    public static Student toEntity(StudentDto dto) {
        Student student = new Student();
        student.setUsername(dto.getUsername());
        student.setStudentName(dto.getStudentName());
        student.setAm(dto.getAm());
        student.setAvgGrade(dto.getAvgGrade());
        student.setPreferredLocation(dto.getPreferredLocation());
        student.setInterests(dto.getInterests());
        student.setSkills(dto.getSkills());
        student.setLookingForTraineeship(dto.isLookingForTraineeship());
        return student;
    }
}