package com.alvanklaveren.usecase.administrator;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.*;
import com.mysql.cj.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("AdministratorUserUseCase")
public class AdministratorUserUseCase {
    private final ForumUserRepository forumUserRepository;
    private final ClassificationRepository classificationRepository;

    public AdministratorUserUseCase(ForumUserRepository forumUserRepository, ClassificationRepository classificationRepository) {
        this.forumUserRepository = forumUserRepository;
        this.classificationRepository = classificationRepository;
    }

    @Transactional(readOnly=true)
    public List<ForumUserDTO> getUsers() {

        List<ForumUser> forumUsers = forumUserRepository.findAll();
        return ForumUserDTO.toDto(forumUsers, 1);
    }

    @Transactional(readOnly=true)
    public List<ClassificationDTO> getClassifications() {

        List<Classification> classifications = classificationRepository.findAll();
        return ClassificationDTO.toDto(classifications, 1);
    }

    @Transactional
    public ForumUserDTO saveUser(ForumUserDTO forumUserDTO) {

        if(StringUtils.isNullOrEmpty(forumUserDTO.emailAddress)) {
            // without an email address, user cannot receive a (new) password
            throw new RuntimeException("Email address is missing.");
        }

        ForumUser forumUser;

        if (forumUserDTO.code == null || forumUserDTO.code <= 0) {

            EClassification eClassification = EClassification.get(forumUserDTO);
            forumUser = createNewUser(eClassification);
        } else {

            forumUser = forumUserRepository.getByCode(forumUserDTO.code);
            Classification classification = classificationRepository.getByCode(forumUserDTO.classification.code);
            forumUser.setClassification(classification);
        }

        forumUser.setDisplayName(forumUserDTO.displayName);
        forumUser.setUsername(forumUserDTO.username);
        forumUser.setPassword(forumUserDTO.password);
        forumUser.setEmailAddress(forumUserDTO.emailAddress);
        forumUser = forumUserRepository.save(forumUser);

        return ForumUserDTO.toDto(forumUser, 1);
    }


    @Transactional
    public boolean deleteUser(Integer codeForumUser) {

        try {
            ForumUser forumUser = forumUserRepository.getByCode(codeForumUser);
            forumUserRepository.delete(forumUser);
        } catch(Exception e) {
            // delete fails when user either does not exist or user is already connected to forum messages.
            return false;
        }

        return true;
    }

    private ForumUser createNewUser(EClassification eClassification) {

        ForumUser forumUser = new ForumUser();

        Classification classification = switch(eClassification) {
            case Administrator, Unknown -> classificationRepository.getByCode(EClassification.Guest.getCode());
            default -> classificationRepository.getByCode(eClassification.getCode());
        };

        forumUser.setClassification(classification);

        return forumUser;
    }
}
