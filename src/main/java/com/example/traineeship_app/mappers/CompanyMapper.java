package com.example.traineeship_app.mappers;

import com.example.traineeship_app.domainmodel.Company;
import com.example.traineeship_app.Dto.CompanyDto;

public class CompanyMapper {

    public static CompanyDto toDto(Company company) {
        CompanyDto dto =  new CompanyDto();
        dto.setUsername(company.getUsername());
        dto.setCompanyName(company.getCompanyName());
        dto.setCompanyLocation(company.getCompanyLocation());
        return dto;
    }

    public static Company toEntity(CompanyDto dto) {
        Company company = new Company();
        company.setUsername(dto.getUsername());
        company.setCompanyName(dto.getCompanyName());
        company.setCompanyLocation(dto.getCompanyLocation());
        return company;
    }
}
