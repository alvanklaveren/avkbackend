package com.alvanklaveren.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDTO {

    public Integer code;
    public String description;
    public String messageText;
    public Date messageDate;
    public int version;

    public MessageCategoryDTO messageCategory;
    public ForumUserDTO forumUser;
    public MessageDTO message;


    public static List<MessageDTO> toDto(List<Message> messages, int level){
        return messages.stream().map(m -> MessageDTO.toDto(m, level)).collect(Collectors.toList());
    }

    public static MessageDTO toDto(Message message, int level) {
        if (message == null) {
            return null;
        }

        MessageDTO dto = new MessageDTO();
        dto.code = message.getCode();
        dto.description = message.getDescription();
        dto.messageText = message.getMessageText();
        dto.messageDate = message.getMessageDate();
        dto.version = message.getVersion();

        if(--level >= 0) {
            dto.messageCategory = MessageCategoryDTO.toDto(message.getMessageCategory(), level);
            dto.forumUser = ForumUserDTO.toDto(message.getForumUser(), level);
            dto.message = MessageDTO.toDto(message.getMessage(), level);
        }

        return dto;
    }

}
