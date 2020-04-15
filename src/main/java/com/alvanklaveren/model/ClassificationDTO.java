package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class ClassificationDTO {

    public Integer code;
    public String description;
    public boolean isAdmin;
    public int version;

    public static List<ClassificationDTO> toDto(List<Classification> classifications){
        return classifications.stream().map(ClassificationDTO::toDto).collect(Collectors.toList());
    }

    public static ClassificationDTO toDto(Classification classification) {

        if (classification == null) {
            return null;
        }

        ClassificationDTO dto = new ClassificationDTO();
        dto.code = classification.getCode();
        dto.description = classification.getDescription();
        dto.isAdmin = classification.isAdmin();
        dto.version = classification.getVersion();

        return dto;
    }
}
