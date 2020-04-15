package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyDTO {

    public Integer code;
    public String description;
    public int version;

    public static List<CompanyDTO> toDto(List<Company> companies){
        return companies.stream().map(CompanyDTO::toDto).collect(Collectors.toList());
    }

    public static CompanyDTO toDto(Company company) {

        if (company == null) {
            return null;
        }

        CompanyDTO dto = new CompanyDTO();
        dto.code = company.getCode();
        dto.description = company.getDescription();
        dto.version = company.getVersion();

        return dto;
    }

}
