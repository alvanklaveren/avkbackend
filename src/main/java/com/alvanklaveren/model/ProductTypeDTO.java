package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class ProductTypeDTO {

    public Integer code;
    public String description;
    public int version;

    public static List<ProductTypeDTO> toDto(List<ProductType> productTypes){
        return productTypes.stream().map(ProductTypeDTO::toDto).collect(Collectors.toList());
    }

    public static ProductTypeDTO toDto(ProductType productType) {

        if (productType == null) {
            return null;
        }

        ProductTypeDTO dto = new ProductTypeDTO();
        dto.code = productType.getCode();
        dto.description = productType.getDescription();
        dto.version = productType.getVersion();
        return dto;
    }

}