package com.alvanklaveren.model;

import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

public class ProductImageDTO extends AbstractDTO{

    public Integer code;
    public Blob image;
    public int sortorder;
    public int version;

    public ProductDTO product;

    public static List<ProductImageDTO> toDto(List<ProductImage> productImages, int level){
        return productImages.stream().map(p -> ProductImageDTO.toDto(p, level)).toList();
    }

    public static ProductImageDTO toDto(ProductImage productImage, int level) {
        if (productImage == null) {
            return null;
        }

        ProductImageDTO dto = new ProductImageDTO();
        dto.code = productImage.getCode();
        dto.image = productImage.getImage();
        dto.sortorder = productImage.getSortorder();
        dto.version = productImage.getVersion();

        if(--level >= 0) {
            dto.product = ProductDTO.toDto(productImage.getProduct(), level);
        }

        return dto;
    }

}
