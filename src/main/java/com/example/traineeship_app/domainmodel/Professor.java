package com.example.traineeship_app.domainmodel;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "professors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String professorName;

    @ElementCollection
    @CollectionTable(name = "professor_interests", joinColumns = @JoinColumn(name = "professor_id"))
    @Column(name = "interest")
    private List<String> interests;

    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TraineeshipPosition> supervisedPositions;
}
