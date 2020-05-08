package com.alvanklaveren.enums;

public enum EClassification {

    Unknown(0, "<unknown"),
    Administrator(1,"administrator"),
    Member(2, "member"),
    Guest(3, "guest");

    private int code;
    private String description;

    EClassification(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static EClassification getByCode(int code){

        for(EClassification value:values()){
            if(value.getCode() ==code){
                return value;
            }
        }
        return Unknown;
    }
}
