package com.alvanklaveren.usecase.administrator;

import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;


@Service("AdministratorConstantsUseCase")
@Slf4j
@AllArgsConstructor
public class AdministratorConstantsUseCase {

    @Autowired private final ConstantsRepository constantsRepository;


    @Transactional(readOnly = true)
    public ConstantsDTO getByCode(Integer code) {

        Constants constants = constantsRepository.getByCode(code);
        return ConstantsDTO.toDto(constants, 0);
    }

    @Transactional(readOnly = true)
    public ConstantsDTO getById(String id) {

        List<Constants> constantsList = constantsRepository.getById(id);
        if(constantsList.size() == 0){
            return null;
        }

        return ConstantsDTO.toDto(constantsList.get(0), 0);
    }

    @Transactional
    public ConstantsDTO save(ConstantsDTO constantsDTO) {

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

    @Transactional
    public ConstantsDTO uploadImageAlt(Integer codeConstants, String fileContent){

        byte[] imageByte = Base64.decodeBase64(fileContent.getBytes());

        Constants constants = constantsRepository.getByCode(codeConstants);

        try {
            Blob blob = new SerialBlob(imageByte);
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

}
