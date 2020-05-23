package com.alvanklaveren.enums;

import com.alvanklaveren.model.ClassificationDTO;
import com.alvanklaveren.model.ForumUserDTO;

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
            if(value.getCode() == code){
                return value;
            }
        }
        return Unknown;
    }

    public static EClassification get(ClassificationDTO classificationDTO){
        return getByCode(classificationDTO.code);
    }

    public static EClassification get(ForumUserDTO forumUserDTO){
        return getByCode(forumUserDTO.classification.code);
    }

}
