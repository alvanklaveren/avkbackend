package com.alvanklaveren.usecase;

import com.alvanklaveren.model.Message;
import com.alvanklaveren.model.MessageDTO;
import com.alvanklaveren.model.MessageImage;
import com.alvanklaveren.repository.MessageImageRepository;
import com.alvanklaveren.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utils.StringLogic;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

@Component
public class MessageUseCase {

    @Autowired private MessageRepository messageRepository;
    @Autowired private MessageImageRepository messageImageRepository;


    @Transactional(readOnly=true)
    public List<MessageDTO> getByCategoryCode(Integer codeCategory){

        List<Message> messages = messageRepository.getByMessageCategory_Code(codeCategory, Sort.by("code").descending());

        messages.forEach(message -> {
            String messageText = message.getMessageText();

            messageText = StringLogic.prepareMessage(messageText);
            messageText = setRawImage(messageText);

            message.setMessageText(messageText);
        });

        return MessageDTO.toDto(messages);
    }


    private String setRawImage( String messageText ) {

        String modifiedMessageText = messageText;

        String imageCoded = StringLogic.findFirst(modifiedMessageText, "[i:", "]");

        while (imageCoded.length() > 0) {

            String imgHTML = "[image not found]";

            try {
                int codeImage = Integer.parseInt(imageCoded.replace("[i:", "").replaceAll("]", ""));
                byte[] rawImage = getImage(codeImage);
                byte[] rawImageBase64 = Base64.getEncoder().encode(rawImage);

                if (rawImage != null) {
                    imgHTML = "<img src=\"data:image/jpg;base64,";

                    for(byte b : rawImageBase64) {
                        imgHTML += (char) b;
                    }

                    imgHTML += "\" style=\"width:auto; max-width:100%\"></img>";
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            modifiedMessageText = modifiedMessageText.replace(imageCoded, imgHTML);

            imageCoded = StringLogic.findFirst(modifiedMessageText, "[i:", "]");
        }

        return modifiedMessageText.trim();
    }

    private byte[] getImage(int code){

        MessageImage messageImage = messageImageRepository.getByCode(code);
        byte[] image = null;

        if( messageImage != null ){
            Blob blob = messageImage.getImage();
            try {
                image = messageImage.getImage().getBytes(1, (int) blob.length());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return image;
    }



}
