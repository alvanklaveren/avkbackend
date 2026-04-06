package com.alvanklaveren.enums;

import com.alvanklaveren.model.Translation;

import java.util.Arrays;

public enum ELanguage {

    Unknown(0,"unknown"),
    US(1, "US"),
    NL(2, "NL");

    private final int id;
    private final String isoA2;

    ELanguage(int id, String isoA2) {
        this.id = id;
        this.isoA2 = isoA2;
    }

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

    public String getIsoA2() {
        return isoA2;
    }

    public int getId() {
        return id;
    }
}
