package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class ProductRatingDTO {

    public Integer code;
    public int rating;
    public int version;

    public ProductDTO product;
    public RatingUrlDTO ratingUrl;

    public static List<ProductRatingDTO> toDto(List<ProductRating> productRatings){
        return productRatings.stream().map(ProductRatingDTO::toDto).collect(Collectors.toList());
    }

    public static ProductRatingDTO toDto(ProductRating productRating) {
        if (productRating == null) {
            return null;
        }

        ProductRatingDTO dto = new ProductRatingDTO();
        dto.code = productRating.getCode();
        dto.version = productRating.getVersion();

        dto.product = ProductDTO.toDto(productRating.getProduct());
        dto.ratingUrl = RatingUrlDTO.toDto(productRating.getRatingUrl());

        return dto;
    }

}
