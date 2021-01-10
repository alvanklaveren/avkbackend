package com.alvanklaveren.enums;

import com.alvanklaveren.model.Translation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor
public enum ELanguage {

    Unknown(0,"unknown"),
    US(1, "US"),
    NL(2, "NL");

    private final int id;
    private final String isoA2;

    public String translate(Translation translation){

        if(translation == null){
             return null;
        }

        switch(this){
            case NL:    return translation.getNl();
            case US:    return translation.getUs();
            default:    return translation.getOriginal();
        }
    }

    public static ELanguage getByIsoA2(String isoA2){
        return Arrays.stream(values()).filter(value -> value.getIsoA2().equalsIgnoreCase(isoA2)).findFirst().orElse(Unknown);
    }
}
