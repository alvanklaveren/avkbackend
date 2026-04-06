package com.alvanklaveren.enums;

import java.util.Arrays;

public enum EProductStatus {

    Available(0, "Available"),
    Sold(1, "Sold"),
    ;


    private final int id;
    private final String description;

    EProductStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static EProductStatus getById(int code){
        return Arrays.stream(values()).filter(value -> value.getId() == code).findFirst().orElse(Available);
    }

    public static class EProductStatusDTO {

        public Integer id;
        public String description;
    }

}
