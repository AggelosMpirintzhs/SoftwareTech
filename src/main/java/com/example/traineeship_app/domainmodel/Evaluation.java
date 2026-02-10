package com.example.traineeship_app.domainmodel;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType;

    private int motivation;

    private int efficiency;

    private int effectiveness;

    private int facilities;

    private int guidance;

    @ManyToOne
    @JoinColumn(name = "traineeship_position_id")
    private TraineeshipPosition traineeshipPosition;
}


