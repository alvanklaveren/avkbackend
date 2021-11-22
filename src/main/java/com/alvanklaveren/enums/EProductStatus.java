package com.alvanklaveren.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor
public enum EProductStatus {

    Available(0, "Available"),
    Sold(1, "Sold"),
    ;


    private final int id;
    private final String description;

    public static EProductStatus getById(int code){
        return Arrays.stream(values()).filter(value -> value.getId() == code).findFirst().orElse(Available);
    }

    public static class EProductStatusDTO {

        public Integer id;
        public String description;
    }

}
