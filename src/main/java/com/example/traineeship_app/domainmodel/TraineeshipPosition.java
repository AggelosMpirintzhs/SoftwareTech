package com.example.traineeship_app.domainmodel;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "traineeship_positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TraineeshipPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String topics;

    private String skills;

    private boolean isAssigned;

    private String studentLogbook;

    private boolean passFailGrade;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor supervisor;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "traineeshipPosition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evaluation> evaluations;
}
