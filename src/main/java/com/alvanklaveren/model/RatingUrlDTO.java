package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class RatingUrlDTO extends AbstractDTO{

    public Integer code;
    public String url;
    public int version;

    public static List<RatingUrlDTO> toDto(List<RatingUrl> ratingUrls, int level){
        return ratingUrls.stream().map(r -> RatingUrlDTO.toDto(r, level)).toList();
    }

    public static RatingUrlDTO toDto(RatingUrl ratingUrl, int level) {

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
