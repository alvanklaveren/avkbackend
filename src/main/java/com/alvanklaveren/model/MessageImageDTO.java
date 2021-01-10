package com.alvanklaveren.model;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class MessageImageDTO extends AbstractDTO{

    public Integer code;
    public byte[] image;
    public int sortorder;
    public int version;

    public MessageDTO message;

    public static List<MessageImageDTO> toDto(List<MessageImage> messageImages, int level){
        return messageImages.stream().map(m -> MessageImageDTO.toDto(m, level)).collect(Collectors.toList());
    }

    public static MessageImageDTO toDto(MessageImage messageImage, int level) {
        if (messageImage == null) {
            return null;
        }

        MessageImageDTO dto = new MessageImageDTO();
        dto.code = messageImage.getCode();

        try {
            dto.image = messageImage.getImage().getBytes(1, (int) messageImage.getImage().length());
        } catch(SQLException se){
            se.printStackTrace();
        }

        dto.sortorder = messageImage.getSortorder();
        dto.version = messageImage.getVersion();

        if(--level >= 0) {
            dto.message = MessageDTO.toDto(messageImage.getMessage(), level);
        }

        return dto;
    }

}
