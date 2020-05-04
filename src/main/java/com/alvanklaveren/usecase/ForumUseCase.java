package com.alvanklaveren.usecase;

import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.ForumUserRepository;
import com.alvanklaveren.repository.MessageCategoryRepository;
import com.alvanklaveren.repository.MessageImageRepository;
import com.alvanklaveren.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utils.StringLogic;

import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Component
public class ForumUseCase {

    @Autowired private MessageCategoryRepository messageCategoryRepository;
    @Autowired private MessageImageRepository messageImageRepository;
    @Autowired private ForumUserRepository forumUserRepository;
    @Autowired private MessageRepository messageRepository;


    @Transactional
    public MessageDTO save(MessageDTO messageDTO){

        boolean isNewMessage = messageDTO.code == null || messageDTO.code == 0;

        Message message;
        if(isNewMessage) {
            message = new Message();
            message.setMessageDate(Date.from(Instant.now()));
        } else {
            message = messageRepository.getOne(messageDTO.code);
            message.setMessageDate(messageDTO.messageDate);
            message.setVersion(messageDTO.version);
        }

        message.setDescription(messageDTO.description);
        message.setMessageText(messageDTO.messageText);

        MessageCategory messageCategory = messageCategoryRepository.getOne(messageDTO.messageCategory.code);
        message.setMessageCategory(messageCategory);

        ForumUser forumUser;
        if(isNewMessage) {
            // TODO: SPRING SECURITY: Implement and use UserContext to save forumuser.
            forumUser = forumUserRepository.getOne(1);
        } else {
            forumUser = forumUserRepository.getOne(messageDTO.forumUser.code);
        }
        message.setForumUser(forumUser);

        if(messageDTO.message != null && messageDTO.message.code != null) {
            Message linkedMessage = messageRepository.getOne(messageDTO.message.code);
            message.setMessage(linkedMessage);
        }

        message = messageRepository.saveAndFlush(message);

        return MessageDTO.toDto(message, 1);
    }

    @Transactional
    public void delete(Integer codeMessage) {

        Message message = messageRepository.getOne(codeMessage);

        List<Message> replyMessages = messageRepository.findByMessage_Code(message.getCode());
        if(replyMessages != null && replyMessages.size() > 0) {
            messageRepository.deleteAll(replyMessages);
        }

        messageRepository.delete(message);
    }

    @Transactional(readOnly = true)
    public MessageDTO getMessage(Integer codeMessage) {

        Message message = messageRepository.getOne(codeMessage);
        return MessageDTO.toDto(message, 1);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getReplyMessages(Integer codeMessage) {

        List<Message> replyMessages = messageRepository.findByMessage_Code(codeMessage);
        return MessageDTO.toDto(replyMessages, 1);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getByCategoryCode(Integer codeCategory, int page, int pageSize){

        Pageable pageRequest = PageRequest.of(page, pageSize, Sort.by("code").descending());

        List<Message> messages = messageRepository.getByMessageCategory_Code(codeCategory, pageRequest);

        messages.forEach(message -> {
            String messageText = message.getMessageText();

            messageText = StringLogic.prepareMessage(messageText);
            messageText = setRawImage(messageText);

            message.setMessageText(messageText);
        });

        return MessageDTO.toDto(messages, 1);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesByCategory(Integer codeMessageCategory){

        List<Message> messages = messageRepository.findByMessageCategory_Code(codeMessageCategory, Sort.by("messageDate").descending());

        return MessageDTO.toDto(messages, 1);
    }

    @Transactional(readOnly = true)
    public List<MessageCategoryDTO> getMessageCategories(){

        List<MessageCategory> messageCategories = messageCategoryRepository.findAll();
        return MessageCategoryDTO.toDto(messageCategories, 0);
    }

    @Transactional(readOnly = true)
    public MessageCategoryDTO getMessageCategory(Integer codeMessageCategory){

        MessageCategory messageCategory = messageCategoryRepository.getOne(codeMessageCategory);
        return MessageCategoryDTO.toDto(messageCategory, 0);
    }

    @Transactional(readOnly = true)
    public Integer getMessageCount(Integer codeMessageCategory){

        Integer messageCount = messageRepository.countByMessageCategory(codeMessageCategory);
        return messageCount;
    }

    @Transactional(readOnly=true)
    public byte[] getAvatar(int codeForumUser) {

        byte[] image = {};

        ForumUser forumUser = forumUserRepository.getOne(codeForumUser);

        Blob blob = forumUser.getAvatar();
        if(blob == null) { return image; }

        try {
            int blobLength = (int) blob.length();
            image = blob.getBytes(1, blobLength);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return image;
    }

    public String prepareMessage(String messageText) {

        String prepared = StringLogic.prepareMessage(messageText);
        prepared = setRawImage(prepared);

        return prepared;
    }

    private String setRawImage(String messageText) {

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
        if( messageImage == null ) { return null; }

        try {
            Blob blob = messageImage.getImage();
            return blob.getBytes(1, (int) blob.length());

        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }
}
