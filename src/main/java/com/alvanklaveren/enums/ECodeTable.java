package com.alvanklaveren.enums;

public enum ECodeTable {

    Unknown(-1, "Unknown"),
    Companies(0, "Company"),
    GameConsole(1,"Game Console"),
    ProductType(2, "Product Type"),
    RatingUrl(3, "Rating URLs"),
    Translation(4, "Translation");

    private int id;
    private String description;

    ECodeTable(int id, String description){
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static ECodeTable getByCode(int id){

        for(ECodeTable value:values()){
            if(value.getId() == id){
                return value;
            }
        }
        return Unknown;
    }
}
