package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class RatingUrlDTO {

    public Integer code;
    public String url;
    public int version;

    public static List<RatingUrlDTO> toDto(List<RatingUrl> ratingUrls){
        return ratingUrls.stream().map(RatingUrlDTO::toDto).collect(Collectors.toList());
    }

    public static RatingUrlDTO toDto(RatingUrl ratingUrl) {

        if (ratingUrl == null) {
            return null;
        }

        RatingUrlDTO dto = new RatingUrlDTO();
        dto.code = ratingUrl.getCode();
        dto.url = ratingUrl.getUrl();
        dto.version = ratingUrl.getVersion();

        return dto;
    }

}
