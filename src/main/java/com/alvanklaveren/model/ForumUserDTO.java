package com.alvanklaveren.model;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ForumUserDTO extends AbstractDTO{

    public Integer code;
    public String username;
    public String password;
    public String emailAddress;
    public String displayName;
    public byte[] avatar;
    public int version;

    public ClassificationDTO classification;

    public static List<ForumUserDTO> toDto(List<ForumUser> forumUsers, int level){
        return forumUsers.stream().map(f -> ForumUserDTO.toDto(f, level)).collect(Collectors.toList());
    }

    public static ForumUserDTO toDto(ForumUser forumUser, int level) {

        if (forumUser == null) {
            return null;
        }

        ForumUserDTO dto = new ForumUserDTO();
        dto.code = forumUser.getCode();
        dto.username = forumUser.getUsername();
        dto.password = forumUser.getPassword();
        dto.emailAddress = forumUser.getEmailAddress();
        dto.displayName = forumUser.getDisplayName();

        try {
            if(forumUser.getAvatar() != null) {
                dto.avatar = forumUser.getAvatar().getBytes(1, (int) forumUser.getAvatar().length());
            }
        } catch(SQLException se){
            se.printStackTrace();
        }

        dto.version = forumUser.getVersion();

        if(--level >= 0) {
            dto.classification = ClassificationDTO.toDto(forumUser.getClassification(), level);
        }

        return dto;
    }
}
