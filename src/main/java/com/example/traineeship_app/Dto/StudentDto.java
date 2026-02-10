package com.example.traineeship_app.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private String username;
    private String studentName;
    private String am;
    private double avgGrade;
    private String preferredLocation;
    private List<String> interests;
    private List<String> skills;
    private boolean lookingForTraineeship;

    public StudentDto(String username, String studentName, String am, String preferredLocation, List<String> interests, List<String> skills) {
        this.username = username;
        this.studentName = studentName;
        this.am = am;
        this.preferredLocation = preferredLocation;
        this.interests = interests;
        this.skills = skills;
        this.lookingForTraineeship = false;
    }
}

