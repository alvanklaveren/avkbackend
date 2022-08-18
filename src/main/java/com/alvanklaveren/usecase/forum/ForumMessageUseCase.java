package com.alvanklaveren.usecase.forum;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.model.*;
import com.alvanklaveren.projections.MessageListView;
import com.alvanklaveren.repository.*;
import com.alvanklaveren.security.UserContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.alvanklaveren.utils.StringLogic;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class ForumMessageUseCase {

    @Autowired private final MessageCategoryRepository messageCategoryRepository;
    @Autowired private final MessageImageRepository messageImageRepository;
    @Autowired private final ForumUserRepository forumUserRepository;
    @Autowired private final MessageRepository messageRepository;


    @Transactional
    public MessageDTO save(MessageDTO messageDTO){

        boolean isNewMessage = messageDTO.code == null || messageDTO.code == 0;

        Message message;
        if(isNewMessage) {

            message = new Message();
            message.setMessageDate(Date.from(Instant.now()));
        } else {

            message = messageRepository.findById(messageDTO.code).orElse(null);
            if(!isEditable(message)) {
                throw new RuntimeException("Saving failed: User is not owner of this message");
            }

            if(message == null) {
                message = new Message();
            }

            message.setMessageDate(messageDTO.messageDate);
            message.setVersion(messageDTO.version);
        }

        message.setDescription(messageDTO.description);
        message.setMessageText(messageDTO.messageText);

        MessageCategory messageCategory = messageCategoryRepository.findById(messageDTO.messageCategory.code)
                .orElseThrow(() -> new RuntimeException("Failed to save message. Reason: messageCategory is null!"));
        message.setMessageCategory(messageCategory);

        ForumUser forumUser;
        if(isNewMessage) {

            Integer userCode = UserContext.getId() == null ? 0 : UserContext.getId();
            forumUser = forumUserRepository.findById(userCode)
                    .orElseThrow(() -> new RuntimeException("Cannot save message. Reason: ForumUser is missing."));
        } else {

            forumUser = forumUserRepository.findById(messageDTO.forumUser.code)
                    .orElseThrow(() -> new RuntimeException("Cannot save message. Reason: ForumUser is missing."));
        }

        message.setForumUser(forumUser);

        if(messageDTO.message != null && messageDTO.message.code != null) {

            Message linkedMessage = messageRepository.findById(messageDTO.message.code).orElse(null);
            if(linkedMessage != null) {

                message.setMessage(linkedMessage);
            }
        }

        message = messageRepository.saveAndFlush(message);

        return MessageDTO.toDto(message, 1);
    }

    @Transactional
    public MessageCategoryDTO saveMessageCategory(MessageCategoryDTO messageCategoryDTO){

        boolean isNewCategory = messageCategoryDTO.code == null || messageCategoryDTO.code == 0;

        MessageCategory messageCategory;
        if(isNewCategory) {

            messageCategory = new MessageCategory();
        } else {

            messageCategory = messageCategoryRepository.findById(messageCategoryDTO.code).orElseThrow(() ->
                    new RuntimeException("Cannot save Message Category. Reason:Cannot fetch Message Category with id: "
                            + messageCategoryDTO.code));

            messageCategory.setVersion(messageCategoryDTO.version);
        }

        messageCategory.setDescription(messageCategoryDTO.description);

        messageCategory = messageCategoryRepository.saveAndFlush(messageCategory);

        return MessageCategoryDTO.toDto(messageCategory, 1);
    }

    @Transactional
    public void delete(Integer codeMessage) {

        Message message = messageRepository.findById(codeMessage).orElse(null);

        if(!isEditable(message)) {
            throw new RuntimeException("Deletion failed: User is not owner of this message");
        }

        if(message == null) {
            return;
        }

        List<Message> replyMessages = messageRepository.findByMessage_Code(message.getCode());
        if(replyMessages != null && replyMessages.size() > 0) {
            messageRepository.deleteAll(replyMessages);
        }

        messageRepository.delete(message);
    }

    @Transactional
    public void deleteMessageCategory(Integer codeMessageCategory) {

        messageCategoryRepository.findByCode(codeMessageCategory).ifPresent(messageCategoryRepository::delete);
    }

    @Transactional(readOnly = true)
    public MessageDTO getMessage(Integer codeMessage) {

        Message message = messageRepository.findByCode(codeMessage)
                .orElseThrow(() -> new RuntimeException("Could not find message with code: " + codeMessage));

        return MessageDTO.toDto(message, 1);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getReplyMessages(Integer codeMessage) {

        List<Message> replyMessages = messageRepository.findByMessage_Code(codeMessage);
        return MessageDTO.toDto(replyMessages, 1);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getByCategoryCode(Integer codeCategory, int page, int pageSize, int level){

        Pageable pageRequest = PageRequest.of(page, pageSize, Sort.by("code").descending());

        List<Message> messages = messageRepository.getByMessageCategory_Code(codeCategory, pageRequest);

        messages.forEach(message -> {
            String messageText = message.getMessageText();

            messageText = prepareMessage(messageText);
            message.setMessageText(messageText);
        });

        return MessageDTO.toDto(messages, level);
    }

    @Transactional(readOnly = true)
    public List<MessageListView> getMessagesByCategory(Integer codeMessageCategory){

        return messageRepository.findByMessageCategory_Code(codeMessageCategory,
                Sort.by("messageDate").descending());
    }

    @Transactional(readOnly = true)
    public List<MessageCategoryDTO> getMessageCategories(){

        List<MessageCategory> messageCategories = messageCategoryRepository.findAll();

        // remove the Homepage (admin only) category when not logged in as Admin
        if (!UserContext.hasRole(EClassification.Administrator)) {

            messageCategories.stream()
                    .filter(messageCategory -> messageCategory.getCode().equals(-1))
                    .findFirst()
                    .ifPresent(messageCategories::remove);
        }

        return MessageCategoryDTO.toDto(messageCategories, 0);
    }

    @Transactional(readOnly = true)
    public MessageCategoryDTO getMessageCategory(Integer codeMessageCategory){

        MessageCategory messageCategory = messageCategoryRepository.findById(codeMessageCategory).orElse(null);
        return MessageCategoryDTO.toDto(messageCategory, 0);
    }

    @Transactional(readOnly = true)
    public Integer getMessageCount(Integer codeMessageCategory){

        return messageRepository.countByMessageCategory(codeMessageCategory);
    }

    public String prepareMessage(String messageText) {

        String prepared = StringLogic.prepareMessage(messageText);
        prepared = replaceRawImage(prepared);

        return prepared;
    }

    private String replaceRawImage(String messageText) {

        String modifiedMessageText = messageText;
        String imageCoded = StringLogic.findFirst(modifiedMessageText, "[i:", "]");
        while (imageCoded.length() > 0) {

            StringBuilder imgHTML = new StringBuilder("[image not found]");
            try {
                int codeImage = Integer.parseInt(imageCoded.replace("[i:", "").replaceAll("]", ""));
                byte[] rawImage = getImage(codeImage);
                byte[] rawImageBase64 = Base64.getEncoder().encode(rawImage);

                if (rawImage != null) {
                    imgHTML = new StringBuilder("<img src=\"data:image/jpg;base64,");

                    for(byte b : rawImageBase64) {
                        imgHTML.append((char) b);
                    }

                    imgHTML.append("\" style=\"width:auto; max-width:100%\"></img>");
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            modifiedMessageText = modifiedMessageText.replace(imageCoded, imgHTML.toString());

            imageCoded = StringLogic.findFirst(modifiedMessageText, "[i:", "]");
        }

        return modifiedMessageText.trim();
    }

    @Transactional(readOnly=true)
    public byte[] getImage(int code){

        MessageImage messageImage = messageImageRepository.getByCode(code);
        if( messageImage == null ) {
            return null;
        }

        try {
            Blob blob = messageImage.getImage();
            return blob.getBytes(1, (int) blob.length());

        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    @Transactional
    public MessageImageDTO uploadImage(Integer codeMessage, MultipartFile file){

        if(codeMessage == null || codeMessage <= 0) {
            throw new RuntimeException("Cannot upload image because message not found with id <= 0");
        }

        Message message = messageRepository.findById(codeMessage).orElseThrow(() ->
                new RuntimeException("Cannot upload image because message not found with id: " + codeMessage));

        MessageImage messageImage = new MessageImage();
        messageImage.setMessage(message);
        messageImage.setSortorder(0);

        try {
            Blob blob = new SerialBlob(file.getBytes());
            messageImage.setImage(blob);
        } catch (Exception e){
            e.printStackTrace();
        }

        messageImage = messageImageRepository.save(messageImage);

        return MessageImageDTO.toDto(messageImage, 0);
    }

    @Transactional
    public MessageImageDTO uploadImageAlt(Integer codeMessage, String fileContent){

        byte[] imageByte = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(fileContent.getBytes());

        Message message = null;
        if(codeMessage > 0) {
            message = messageRepository.findByCode(codeMessage)
                    .orElseThrow(() -> new RuntimeException("Could not find message with code: " + codeMessage));
        }

        MessageImage messageImage = new MessageImage();
        messageImage.setMessage(message);
        messageImage.setSortorder(0);

        try {
            Blob blob = new SerialBlob(imageByte);
            messageImage.setImage(blob);
        } catch (Exception e){
            e.printStackTrace();
        }

        messageImage = messageImageRepository.save(messageImage);

        return MessageImageDTO.toDto(messageImage, 0);
    }

    @Transactional(readOnly = true)
    public List<MessageImageDTO> getImages() {

        if(UserContext.getId() == null) {
            throw new RuntimeException("Fetching images failed: No active user found.");
        }

        ForumUser forumUser = forumUserRepository.findById(UserContext.getId())
                .orElseThrow(() -> new RuntimeException("Cannot fetch images. Reason: forum user not found with id: " + UserContext.getId()));

        List<MessageImage> messageImages = messageImageRepository.findAll(forumUser.getCode());
        return MessageImageDTO.toDto(messageImages, 1);
    }

    private boolean isEditable(Message message) {

        if(UserContext.hasRole(EClassification.Administrator) || message == null || message.getForumUser() == null) {
            return true;
        }

        return message.getForumUser().getCode().equals(UserContext.getId());
    }
}
