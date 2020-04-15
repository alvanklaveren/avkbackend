package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class MessageCategoryDTO {

    public Integer code;
    public String description;
    public int version;

    public static List<MessageCategoryDTO> toDto(List<MessageCategory> messageCategories){
        return messageCategories.stream().map(MessageCategoryDTO::toDto).collect(Collectors.toList());
    }

    public static MessageCategoryDTO toDto(MessageCategory messageCategory) {

        if (messageCategory == null) {
            return null;
        }

        MessageCategoryDTO dto = new MessageCategoryDTO();
        dto.code = messageCategory.getCode();
        dto.description = messageCategory.getDescription();
        dto.version = messageCategory.getVersion();
        return dto;
    }

}
