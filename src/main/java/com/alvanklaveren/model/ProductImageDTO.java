package com.alvanklaveren.model;

import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

public class ProductImageDTO {

    public Integer code;
    public Blob image;
    public int sortorder;
    public int version;

    public ProductDTO product;

    public static List<ProductImageDTO> toDto(List<ProductImage> productImages){
        return productImages.stream().map(ProductImageDTO::toDto).collect(Collectors.toList());
    }

    public static ProductImageDTO toDto(ProductImage productImage) {
        if (productImage == null) {
            return null;
        }

        ProductImageDTO dto = new ProductImageDTO();
        dto.code = productImage.getCode();
        dto.image = productImage.getImage();
        dto.sortorder = productImage.getSortorder();
        dto.version = productImage.getVersion();

        dto.product = ProductDTO.toDto(productImage.getProduct());

        return dto;
    }

}
