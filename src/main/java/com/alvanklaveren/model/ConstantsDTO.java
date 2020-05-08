package com.alvanklaveren.model;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ConstantsDTO {

    public Integer code;
    public String id;
    public String stringValue;
    public byte[] blobValue;
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
        try {
            Blob blob = constants.getBlobValue();
            if(blob != null) {
                dto.blobValue = blob.getBytes(1, (int) blob.length());
            }
        } catch( SQLException se) {
            se.printStackTrace();
        }
        dto.version = constants.getVersion();

        return dto;
    }

}
