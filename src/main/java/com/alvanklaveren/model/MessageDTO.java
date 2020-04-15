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


    public static List<MessageDTO> toDto(List<Message> messages){
        return messages.stream().map(MessageDTO::toDto).collect(Collectors.toList());
    }

    public static MessageDTO toDto(Message message) {
        if (message == null) {
            return null;
        }

        MessageDTO dto = new MessageDTO();
        dto.code = message.getCode();
        dto.description = message.getDescription();
        dto.messageText = message.getMessageText();
        dto.messageDate = message.getMessageDate();
        dto.version = message.getVersion();

        dto.messageCategory = MessageCategoryDTO.toDto(message.getMessageCategory());
        dto.forumUser = ForumUserDTO.toDto(message.getForumUser());
        dto.message = MessageDTO.toDto(message.getMessage());

        return dto;
    }

}
