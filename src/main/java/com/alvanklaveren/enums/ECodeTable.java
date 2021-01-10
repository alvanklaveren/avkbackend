package com.alvanklaveren.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor
public enum ECodeTable {

    Unknown(-1, "Unknown"),
    Companies(0, "Company"),
    GameConsole(1,"Game Console"),
    ProductType(2, "Product Type"),
    RatingUrl(3, "Rating URLs"),
    Translation(4, "Translation");

    private final int id;
    private final String description;

    public static ECodeTable getByCode(int id){
        return Arrays.stream(values()).filter(value -> value.getId() == id).findFirst().orElse(Unknown);
    }
}
