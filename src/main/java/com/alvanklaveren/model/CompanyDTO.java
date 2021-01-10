package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyDTO extends AbstractDTO {

    public Integer code;
    public String description;
    public int version;

    public static List<CompanyDTO> toDto(List<Company> companies, int level){
        return companies.stream().map(c -> CompanyDTO.toDto(c, level)).collect(Collectors.toList());
    }

    public static CompanyDTO toDto(Company company, int level) {

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
