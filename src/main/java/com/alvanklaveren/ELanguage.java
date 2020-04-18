package com.alvanklaveren;

import com.alvanklaveren.model.Translation;

import java.util.Arrays;

public enum ELanguage {

    Unknown(0,""),
    US(1, "US"),
    NL(2, "NL");

    private int id;
    private String isoA2;

    ELanguage(int id, String isoA2){
        this.id = id;
        this.isoA2 = isoA2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsoA2() {
        return isoA2;
    }

    public void setIsoA2(String isoA2) {
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

    public static ELanguage getByisoA2(String isoA2){
        return Arrays.stream(values())
                .filter(value -> value.getIsoA2().equalsIgnoreCase(isoA2)).findAny().orElse(Unknown);
    }
}
