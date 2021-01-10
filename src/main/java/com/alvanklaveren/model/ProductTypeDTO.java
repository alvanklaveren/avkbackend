package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class ProductTypeDTO extends AbstractDTO{

    public Integer code;
    public String description;
    public int version;

    public ProductTypeDTO(){ }

    public ProductTypeDTO(Integer code, String description){
        this.code = code;
        this.description = description;
        version = 0;
    }

    public static List<ProductTypeDTO> toDto(List<ProductType> productTypes, int level){
        return productTypes.stream().map(p -> ProductTypeDTO.toDto(p, level)).collect(Collectors.toList());
    }

    public static ProductTypeDTO toDto(ProductType productType, int level) {

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
