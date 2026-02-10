package com.example.traineeship_app.mappers;

import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.Dto.TraineeshipPositionDto;

public class TraineeshipPositionMapper {

    public static TraineeshipPosition toEntity(TraineeshipPositionDto dto) {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setTitle(dto.getTitle());
        position.setDescription(dto.getDescription());
        position.setFromDate(dto.getFromDate());
        position.setToDate(dto.getToDate());
        position.setTopics(dto.getTopics());
        position.setSkills(dto.getSkills());
        position.setAssigned(dto.getIsAssigned() != null && dto.getIsAssigned());
        return position;
    }

    public static TraineeshipPositionDto toDto(TraineeshipPosition position) {
        TraineeshipPositionDto dto = new TraineeshipPositionDto();
        dto.setTitle(position.getTitle());
        dto.setDescription(position.getDescription());
        dto.setFromDate(position.getFromDate());
        dto.setToDate(position.getToDate());
        dto.setTopics(position.getTopics());
        dto.setSkills(position.getSkills());
        dto.setIsAssigned(position.isAssigned());
        return dto;
    }
}
