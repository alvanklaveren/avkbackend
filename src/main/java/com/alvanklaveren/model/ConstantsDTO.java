package com.alvanklaveren.model;

import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

public class ConstantsDTO {

    public Integer code;
    public String id;
    public String stringValue;
    public Blob blobValue;
    public int version;

    public static List<ConstantsDTO> toDto(List<Constants> constants, int level){
        return constants.stream().map(c -> ConstantsDTO.toDto(c, level)).collect(Collectors.toList());
    }

    public static ConstantsDTO toDto(Constants constants, int level) {

        if (constants == null) {
            return null;
        }

        ConstantsDTO dto = new ConstantsDTO();
        dto.code = constants.getCode();
        dto.id = constants.getId();
        dto.stringValue = constants.getStringValue();
        dto.blobValue = constants.getBlobValue();
        dto.version = constants.getVersion();

        return dto;
    }

}
