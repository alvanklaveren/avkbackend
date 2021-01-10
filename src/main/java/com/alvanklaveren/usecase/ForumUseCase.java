package com.alvanklaveren.usecase;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.*;
import com.alvanklaveren.security.UserContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.alvanklaveren.utils.StringLogic;
import com.alvanklaveren.utils.email.HotmailMessage;

import javax.mail.MessagingException;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Slf4j
@AllArgsConstructor
public class ForumUseCase {

    @Autowired private final MessageCategoryRepository messageCategoryRepository;
    @Autowired private final MessageImageRepository messageImageRepository;
    @Autowired private final ForumUserRepository forumUserRepository;
    @Autowired private final ConstantsRepository constantsRepository;
    @Autowired private final MessageRepository messageRepository;

    private final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!";


    @Transactional
    public MessageDTO save(MessageDTO messageDTO){

        boolean isNewMessage = messageDTO.code == null || messageDTO.code == 0;

        Message message;
        if(isNewMessage) {
            message = new Message();
            message.setMessageDate(Date.from(Instant.now()));
        } else {
            message = messageRepository.getOne(messageDTO.code);

            if(isNotEditable(message)) {
                throw new RuntimeException("Saving failed: User is not owner of this message");
            }

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
    public MessageCategoryDTO saveMessageCategory(MessageCategoryDTO messageCategoryDTO){

        boolean isNewCategory = messageCategoryDTO.code == null || messageCategoryDTO.code == 0;

        MessageCategory messageCategory;
        if(isNewCategory) {
            messageCategory = new MessageCategory();
        } else {
            messageCategory = messageCategoryRepository.getOne(messageCategoryDTO.code);
            messageCategory.setVersion(messageCategoryDTO.version);
        }

        messageCategory.setDescription(messageCategoryDTO.description);

        messageCategory = messageCategoryRepository.saveAndFlush(messageCategory);

        return MessageCategoryDTO.toDto(messageCategory, 1);
    }

    @Transactional
    public void delete(Integer codeMessage) {

        Message message = messageRepository.getOne(codeMessage);

        if(isNotEditable(message)) {
            throw new RuntimeException("Deletion failed: User is not owner of this message");
        }

        List<Message> replyMessages = messageRepository.findByMessage_Code(message.getCode());
        if(replyMessages != null && replyMessages.size() > 0) {
            messageRepository.deleteAll(replyMessages);
        }

        messageRepository.delete(message);
    }

    @Transactional
    public void deleteMessageCategory(Integer codeMessageCategory) {

        MessageCategory messageCategory = messageCategoryRepository.getOne(codeMessageCategory);

        messageCategoryRepository.delete(messageCategory);
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
    public List<MessageDTO> getByCategoryCode(Integer codeCategory, int page, int pageSize, int level){

        Pageable pageRequest = PageRequest.of(page, pageSize, Sort.by("code").descending());

        List<Message> messages = messageRepository.getByMessageCategory_Code(codeCategory, pageRequest);

        messages.forEach(message -> {
            String messageText = message.getMessageText();

            messageText = StringLogic.prepareMessage(messageText);
            messageText = setRawImage(messageText);

            message.setMessageText(messageText);
        });

        return MessageDTO.toDto(messages, level);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesByCategory(Integer codeMessageCategory){

        List<Message> messages = messageRepository.findByMessageCategory_Code(codeMessageCategory, Sort.by("messageDate").descending());

        return MessageDTO.toDto(messages, 1);
    }

    @Transactional(readOnly = true)
    public List<MessageCategoryDTO> getMessageCategories(){

        List<MessageCategory> messageCategories = messageCategoryRepository.findAll();

        // remove the Homepage (admin only) category when not logged in as Admin
        if (!UserContext.hasRole(EClassification.Administrator)) {

            messageCategories.stream().filter(mc -> mc.getCode().equals(-1)).findFirst()
                    .ifPresent(messageCategories::remove);
        }

        return MessageCategoryDTO.toDto(messageCategories, 0);
    }

    @Transactional(readOnly = true)
    public MessageCategoryDTO getMessageCategory(Integer codeMessageCategory){

        MessageCategory messageCategory = messageCategoryRepository.getOne(codeMessageCategory);
        return MessageCategoryDTO.toDto(messageCategory, 0);
    }

    @Transactional(readOnly = true)
    public Integer getMessageCount(Integer codeMessageCategory){

        return messageRepository.countByMessageCategory(codeMessageCategory);
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

        Message message = null;
        if(codeMessage > 0) {
            message = messageRepository.getOne(codeMessage);
        }

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
            message = messageRepository.getOne(codeMessage);
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

        ForumUser forumUser = forumUserRepository.getOne(UserContext.getId());

        List<MessageImage> messageImages = messageImageRepository.findAll(forumUser.getCode());
        return MessageImageDTO.toDto(messageImages, 1);
    }

    @Transactional(readOnly = true)
    public ForumUserDTO getForumUser(Integer code) {

        ForumUser forumUser = forumUserRepository.getOne(code);
        return ForumUserDTO.toDto(forumUser, 1);
    }

    @Transactional
    public boolean emailNewPassword(String username){

        ForumUser forumUser = forumUserRepository.getByUsername(username);

        String newPassword = randomPassword();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        forumUser.setPassword(passwordEncoder.encode(username + newPassword));
        forumUser = forumUserRepository.save(forumUser);

        String to = forumUser.getEmailAddress();
        String from = getConstantsStringValueById("email_address");
        String fromPassword = getConstantsStringValueById("email_password");

        HotmailMessage hotmailMessage = new HotmailMessage(to, from, fromPassword);
        hotmailMessage.setSubject("Your password at alvanklaveren.com has been reset");
        hotmailMessage.setBody("Username: " + username + "\nPassword: " + newPassword);

        try {
            hotmailMessage.send();
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private String randomPassword() {

        StringBuilder builder = new StringBuilder();

        IntStream.range(1, 12).forEach(i -> {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        });

        return builder.toString();
    }

    @Transactional(readOnly = true)
    public String getConstantsStringValueById(String id){

        List<Constants> constantsList = constantsRepository.getById(id);
        return constantsList.size() == 0 ? null : ConstantsDTO.toDto(constantsList.get(0), 0).stringValue;
    }

    private boolean isNotEditable(Message message) {

        if(UserContext.hasRole(EClassification.Administrator) || message == null || message.getForumUser() == null) {
            return false;
        }

        return !message.getForumUser().getCode().equals(UserContext.getId());
    }
}
