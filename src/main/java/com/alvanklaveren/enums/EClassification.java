package com.alvanklaveren.enums;

import com.alvanklaveren.model.Classification;
import com.alvanklaveren.model.ClassificationDTO;
import com.alvanklaveren.model.ForumUser;
import com.alvanklaveren.model.ForumUserDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor
public enum EClassification {

    Unknown(0, "<unknown>", "ROLE_UNKNOWN"),
    Administrator(1,"administrator", "ROLE_ADMIN"),
    Member(2, "member", "ROLE_MEMBER"),
    Guest(3, "guest", "ROLE_GUEST");

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MEMBER = "ROLE_MEMBER";
    public static final String ROLE_GUEST = "ROLE_GUEST";

    private final int code;
    private final String description;
    private final String roleName;

    public static EClassification getByCode(int code){
        return Arrays.stream(values()).filter(value -> value.getCode() == code).findFirst().orElse(Unknown);
    }

    public static EClassification get(ClassificationDTO classificationDTO){
        return getByCode(classificationDTO.code);
    }

    public static EClassification get(ForumUserDTO forumUserDTO){
        return getByCode(forumUserDTO.classification.code);
    }

    public static EClassification get(Classification classification){
        return getByCode(classification.getCode());
    }

    public static EClassification get(ForumUser forumUser){
        return getByCode(forumUser.getClassification().getCode());
    }

}
