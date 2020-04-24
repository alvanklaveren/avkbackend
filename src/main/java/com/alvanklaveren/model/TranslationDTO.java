package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class TranslationDTO {

    public Integer code;
    public String original;
    public String us;
    public String nl;
    public int version;

    public static List<TranslationDTO> toDto(List<Translation> translations, int level){
        return translations.stream().map(t -> TranslationDTO.toDto(t, level)).collect(Collectors.toList());
    }

    public static TranslationDTO toDto(Translation translation, int level) {

        if (translation == null) {
            return null;
        }

        TranslationDTO dto = new TranslationDTO();
        dto.code = translation.getCode();
        dto.original = translation.getOriginal();
        dto.us = translation.getUs();
        dto.nl = translation.getNl();
        dto.version = translation.getVersion();

        return dto;
    }
}
