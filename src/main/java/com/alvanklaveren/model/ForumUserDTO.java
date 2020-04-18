package com.alvanklaveren.model;

import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

public class ForumUserDTO {

    public Integer code;
    public String username;
    public String password;
    public String emailAddress;
    public String displayName;
    private Blob avatar;
    public int version;

    private ClassificationDTO classification;

    public static List<ForumUserDTO> toDto(List<ForumUser> forumUsers){
        return forumUsers.stream().map(ForumUserDTO::toDto).collect(Collectors.toList());
    }

    public static ForumUserDTO toDto(ForumUser forumUser) {

        if (forumUser == null) {
            return null;
        }

        ForumUserDTO dto = new ForumUserDTO();
        dto.code = forumUser.getCode();
        dto.username = forumUser.getUsername();
        dto.password = forumUser.getPassword();
        dto.emailAddress = forumUser.getEmailAddress();
        dto.displayName = forumUser.getDisplayName();
        dto.avatar = forumUser.getAvatar();
        dto.version = forumUser.getVersion();

        dto.classification = ClassificationDTO.toDto(forumUser.getClassification());

        return dto;
    }
}
