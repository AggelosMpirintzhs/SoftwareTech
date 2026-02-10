package com.example.traineeship_app.domainmodel;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false, unique = true)
    private String am;

    @Column(nullable = false)
    private double avgGrade;

    @Column(name = "preferred_location")
    private String preferredLocation;

    @ElementCollection
    @CollectionTable(name = "student_interests", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "interest")
    private List<String> interests;

    @ElementCollection
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill")
    private List<String> skills;

    @Column(name = "looking_for_traineeship")
    private boolean lookingForTraineeship;

    @OneToOne(mappedBy = "student")
    private TraineeshipPosition assignedTraineeship;

}