package com.alvanklaveren.usecase;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.ClassificationRepository;
import com.alvanklaveren.repository.ConstantsRepository;
import com.alvanklaveren.repository.ForumUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Component
public class AdministratorUseCase {

    @Autowired private ConstantsRepository constantsRepository;
    @Autowired private ForumUserRepository forumUserRepository;
    @Autowired private ClassificationRepository classificationRepository;

    @Transactional(readOnly = true)
    public ConstantsDTO getByCode(Integer code){
        Constants constants = constantsRepository.getByCode(code);
        return ConstantsDTO.toDto(constants, 0);
    }

    @Transactional(readOnly = true)
    public ConstantsDTO getById(String id){
        List<Constants> constantsList = constantsRepository.getById(id);
        if(constantsList.size() == 0){
            return null;
        }

        return ConstantsDTO.toDto(constantsList.get(0), 0);
    }

    @Transactional
    public ConstantsDTO save(ConstantsDTO constantsDTO){

        Constants constants = constantsRepository.getByCode(constantsDTO.code);
        constants.setStringValue(constantsDTO.stringValue);

        try {
            if(constantsDTO.blobValue != null && constantsDTO.blobValue.length > 0) {
                Blob blob = new SerialBlob(constantsDTO.blobValue);
                constants.setBlobValue(blob);
            }
            constants.setBlobValue(null);
        } catch (Exception e){
            e.printStackTrace();
        }

        constants = constantsRepository.save(constants);

        return ConstantsDTO.toDto(constants, 0);
    }

    @Transactional
    public ConstantsDTO uploadImage(Integer codeConstants, MultipartFile file){

        Constants constants = constantsRepository.getByCode(codeConstants);

        try {
            Blob blob = new SerialBlob(file.getBytes());
            constants.setBlobValue(blob);
        } catch (Exception e){
            e.printStackTrace();
        }

        constants = constantsRepository.save(constants);

        return ConstantsDTO.toDto(constants, 0);
    }

    @Transactional(readOnly=true)
    public byte[] getConstantsImage(Integer codeConstants) {

        byte[] image = {};

        Constants constants = constantsRepository.getByCode(codeConstants);

        try {
            Blob blob = constants.getBlobValue();
            if(blob != null) {
                int blobLength = (int) blob.length();
                image = blob.getBytes(1, blobLength);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Transactional(readOnly=true)
    public List<ForumUserDTO> getUsers(){
        List<ForumUser> forumUsers = forumUserRepository.findAll();
        return ForumUserDTO.toDto(forumUsers, 1);
    }

    @Transactional(readOnly=true)
    public List<ClassificationDTO> getClassifications(){
        List<Classification> classifications = classificationRepository.findAll();
        return ClassificationDTO.toDto(classifications, 1);
    }

    @Transactional
    public ForumUserDTO saveUser(ForumUserDTO forumUserDTO){

        ForumUser forumUser;
        Classification classification;

        if (forumUserDTO.code == null || forumUserDTO.code <= 0) {
            forumUser = new ForumUser();
            classification = classificationRepository.getByCode(EClassification.Guest.getCode());
        } else {
            forumUser = forumUserRepository.getByCode(forumUserDTO.code);
            classification = classificationRepository.getByCode(forumUserDTO.classification.code);
        }


        forumUser.setCode(forumUserDTO.code);
        forumUser.setDisplayName(forumUserDTO.displayName);
        forumUser.setEmailAddress(forumUserDTO.emailAddress);
        forumUser.setUsername(forumUserDTO.username);
        forumUser.setPassword(forumUserDTO.password);
        forumUser.setClassification(classification);

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

}
