package com.example.traineeship_app.Dto;

import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PositionWithScoreDto {
    private TraineeshipPosition position;
    private int totalScore;
    private int skillsMatched;
    private int interestsMatched;
    private boolean locationMatch;
    private int totalRequiredSkills;
    private int totalRequiredInterests;

}

