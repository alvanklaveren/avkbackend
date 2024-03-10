package com.alvanklaveren.usecase.forum;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.ConstantsRepository;
import com.alvanklaveren.repository.ForumUserRepository;
import com.alvanklaveren.security.UserContext;
import com.alvanklaveren.utils.email.HotmailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

@Service("ForumUserUseCase")
public class ForumUserUseCase {
    private final ForumUserRepository forumUserRepository;
    private final ConstantsRepository constantsRepository;

    private final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!";

    public ForumUserUseCase(ForumUserRepository forumUserRepository, ConstantsRepository constantsRepository) {
        this.forumUserRepository = forumUserRepository;
        this.constantsRepository = constantsRepository;
    }


    @Transactional(readOnly=true)
    public byte[] getAvatar(int codeForumUser) {

        byte[] image = {};

        ForumUser forumUser = forumUserRepository.findByCode(codeForumUser)
                .orElseThrow(() -> new RuntimeException("Could not find forum user with code: " + codeForumUser));

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

    @Transactional(readOnly = true)
    public ForumUserDTO getForumUser(Integer code) {

        ForumUser forumUser = forumUserRepository.findById(code).orElse(null);
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

    private String getConstantsStringValueById(String id){

        List<Constants> constantsList = constantsRepository.getById(id);
        return constantsList.size() == 0 ? null : ConstantsDTO.toDto(constantsList.get(0), 0).stringValue;
    }

    private boolean isEditable(Message message) {

        if(UserContext.hasRole(EClassification.Administrator) || message == null || message.getForumUser() == null) {
            return true;
        }

        return message.getForumUser().getCode().equals(UserContext.getId());
    }
}
