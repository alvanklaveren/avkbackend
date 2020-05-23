package com.alvanklaveren.rest;

import com.alvanklaveren.enums.ECodeTable;
import com.alvanklaveren.model.*;
import com.alvanklaveren.usecase.AdministratorUseCase;
import com.alvanklaveren.usecase.ForumUseCase;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.alvanklaveren.security.SecurityConstants.ROLE_ADMIN;

@RestController
@RequestMapping("/backend/administrator")
@Secured({ROLE_ADMIN})
public class AdministratorController {

    private static final Logger LOG = LoggerFactory.getLogger(AdministratorController.class);

    @Autowired private AdministratorUseCase administratorUseCase;
    @Autowired private ForumUseCase forumUseCase;

    @RequestMapping(value = "/getConstant", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<ConstantsDTO> getConstant(@RequestBody Integer codeConstants) {

        ConstantsDTO constantsDTO = administratorUseCase.getByCode(codeConstants);

        return new ResponseEntity<>(constantsDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/getConstantById", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<ConstantsDTO> getConstantById(@RequestBody String codeConstants) {

        ConstantsDTO constantsDTO = administratorUseCase.getById(codeConstants);

        return new ResponseEntity<>(constantsDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/saveConstant", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<ConstantsDTO> saveConstant(@RequestBody ConstantsDTO constantsDTO) {

        constantsDTO = administratorUseCase.save(constantsDTO);
        return new ResponseEntity<>(constantsDTO, HttpStatus.OK);
    }

    @RequestMapping(value="/uploadConstantsImage", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<ConstantsDTO> uploadConstantImage(@RequestParam("imageFile") MultipartFile file, @RequestParam("codeConstants") Integer codeConstants){

        ConstantsDTO constantsDTO = administratorUseCase.uploadImage(codeConstants, file);

        return new ResponseEntity<>(constantsDTO, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getConstantsImage", method = {RequestMethod.GET}, produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getConstantsImage(@RequestParam Integer codeConstants) {

        return administratorUseCase.getConstantsImage(codeConstants);
    }

    @RequestMapping(value = "/getUsers", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<List<ForumUserDTO>> getUsers() {

        List<ForumUserDTO> userDTOs = administratorUseCase.getUsers();

        // remove the passwords... its not safe to send them to the client(browser) !!
        userDTOs.forEach(dto -> dto.password = "");

        return new ResponseEntity<>(userDTOs, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getClassifications", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<List<ClassificationDTO>> getClassifications() {

        List<ClassificationDTO> classificationDTOs = administratorUseCase.getClassifications();
        return new ResponseEntity<>(classificationDTOs, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/saveUser", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<ForumUserDTO> saveUser(@RequestBody ForumUserDTO forumUserDTO) {

        boolean isNewUser = (forumUserDTO.code == null || forumUserDTO.code <= 0);

        forumUserDTO = administratorUseCase.saveUser(forumUserDTO);

        // a new user needs to be emailed a new password
        if(isNewUser) {
            forumUseCase.emailNewPassword(forumUserDTO.username);
        }

        return new ResponseEntity<>(forumUserDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteUser", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<String> deleteUser(@RequestBody Integer codeForumUser) {

        boolean isDeleted = administratorUseCase.deleteUser(codeForumUser);

        JSONObject response = new JSONObject();
        response.put("result", isDeleted ? "true" : "false");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/saveCodeTableRow", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<String> saveCodeTable(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);

        String codeTableRow = jsonObject.get("codeTableRow").toString();

        ECodeTable eCodeTable = ECodeTable.getByCode(jsonObject.getInt("codeTable"));
        switch(eCodeTable){
            case Companies:
                CompanyDTO companyDTO = (CompanyDTO) toDTO(CompanyDTO.class, codeTableRow);
                administratorUseCase.saveCompany(companyDTO);
                break;

            case GameConsole:
                GameConsoleDTO gameConsoleDTO = (GameConsoleDTO) toDTO(GameConsoleDTO.class, codeTableRow);
                administratorUseCase.saveGameConsole(gameConsoleDTO);
                break;

            case ProductType:
                ProductTypeDTO productTypeDTO = (ProductTypeDTO) toDTO(ProductTypeDTO.class, codeTableRow);
                administratorUseCase.saveProductType(productTypeDTO);
                break;

            case RatingUrl:
                RatingUrlDTO ratingUrlDTO = (RatingUrlDTO) toDTO(RatingUrlDTO.class, codeTableRow);
                administratorUseCase.saveRatingUrl(ratingUrlDTO);
                break;
        }

        JSONObject response = new JSONObject();
        response.put("result", "true");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteCodeTableRow", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<String> deleteCodeTableRow(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);

        boolean isDeleted = false;

        Integer code = jsonObject.getInt("code");

        ECodeTable eCodeTable = ECodeTable.getByCode(jsonObject.getInt("codeTable"));
        switch(eCodeTable){
            case Companies:
                isDeleted = administratorUseCase.deleteCompany(code);
                break;
            case GameConsole:
                isDeleted = administratorUseCase.deleteGameConsole(code);
                break;
            case ProductType:
                isDeleted = administratorUseCase.deleteProductType(code);
                break;
            case RatingUrl:
                isDeleted = administratorUseCase.deleteRatingUrl(code);
                break;
        }

        JSONObject response = new JSONObject();
        response.put("result", isDeleted ? "true" : "false");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    public Object toDTO(Class clazz, String content) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(clazz);
        try {
            MappingIterator mappingIterator = reader.readValues(content);
            return mappingIterator.next();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
