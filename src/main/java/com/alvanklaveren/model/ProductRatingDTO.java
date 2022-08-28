package com.alvanklaveren.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductRatingDTO extends AbstractDTO{

    public Integer code;
    public int rating;
    public int version;

    public ProductDTO product;
    public RatingUrlDTO ratingUrl;

    public static Set<ProductRatingDTO> toDto(Set<ProductRating> productRatings, int level){

        return new ArrayList<>(productRatings).stream()
                        .map(p -> ProductRatingDTO.toDto(p, level)).collect(Collectors.toSet());
    }

    public static List<ProductRatingDTO> toDto(List<ProductRating> productRatings, int level){
        return productRatings.stream().map(p -> ProductRatingDTO.toDto(p, level)).toList();
    }

    public static ProductRatingDTO toDto(ProductRating productRating, int level) {
        if (productRating == null) {
            return null;
        }

        ProductRatingDTO dto = new ProductRatingDTO();
        dto.code = productRating.getCode();
        dto.rating = productRating.getRating();
        dto.version = productRating.getVersion();

        if(--level > 0) {
            dto.product = ProductDTO.toDto(productRating.getProduct(), level);
            dto.ratingUrl = RatingUrlDTO.toDto(productRating.getRatingUrl(), level);
        }

        return dto;
    }

}
