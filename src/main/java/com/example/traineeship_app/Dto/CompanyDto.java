package com.example.traineeship_app.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private String username;
    private String companyName;
    private String companyLocation;
    private List<TraineeshipPositionDto> positions;

    public CompanyDto(String username, String companyName, String companyLocation) {
        this.username = username;
        this.companyName = companyName;
        this.companyLocation = companyLocation;
    }
}
